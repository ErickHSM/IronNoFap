package com.example.data.repository

import com.example.data.firestore.FirestoreManager
import com.example.data.local.DayLogDao
import com.example.data.local.DayLogEntity
import com.example.data.local.PunishmentDao
import com.example.data.local.PunishmentEntity
import com.example.model.DailyMissions
import com.example.model.DayRecord
import com.example.model.DayStatus
import com.example.model.DefaultPunishments
import com.example.model.JournalEntry
import com.example.model.MilestoneData
import com.example.model.Punishment
import com.example.model.RankSystem
import com.example.model.UserStats
import com.example.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID

class NoFapRepository(
    private val dayLogDao: DayLogDao,
    private val punishmentDao: PunishmentDao,
    private val firestoreManager: FirestoreManager
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // In-memory / reactive caches for new systems
    private val _dailyMissionsFlow = MutableStateFlow<Map<String, DailyMissions>>(emptyMap())
    private val _journalEntriesFlow = MutableStateFlow<List<JournalEntry>>(emptyList())

    init {
        // Initial dummy missions for today
        val today = DateUtils.getTodayString()
        _dailyMissionsFlow.value = mapOf(today to DailyMissions(today))
    }

    fun getAllDayRecords(): Flow<Map<String, DayRecord>> {
        return dayLogDao.getAllLogs().map { entities ->
            entities.associate { entity ->
                val status = try {
                    DayStatus.valueOf(entity.status)
                } catch (e: Exception) {
                    DayStatus.UNMARKED
                }
                entity.date to DayRecord(
                    date = entity.date,
                    status = status,
                    note = entity.note,
                    urgeLevel = entity.urgeLevel,
                    triggerReason = entity.triggerReason,
                    penaltyAssigned = entity.penaltyAssigned,
                    penaltyCompleted = entity.penaltyCompleted,
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    fun getAllPunishments(): Flow<List<Punishment>> {
        return punishmentDao.getAllPunishments().map { list ->
            list.map { entity ->
                Punishment(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    category = entity.category,
                    intensity = entity.intensity,
                    isCompleted = entity.isCompleted,
                    dateAssigned = entity.dateAssigned,
                    completedAt = entity.completedAt
                )
            }
        }
    }

    fun getPendingPunishmentsCount(): Flow<Int> = punishmentDao.getPendingCount()

    fun getDailyMissions(date: String): Flow<DailyMissions> {
        return _dailyMissionsFlow.map { map ->
            map[date] ?: DailyMissions(date)
        }
    }

    fun getJournalEntries(): Flow<List<JournalEntry>> = _journalEntriesFlow.asStateFlow()

    suspend fun markDayClean(date: String, note: String = "", urgeLevel: Int = 0) {
        val entity = DayLogEntity(
            date = date,
            status = DayStatus.CLEAN.name,
            note = note,
            urgeLevel = urgeLevel,
            updatedAt = System.currentTimeMillis()
        )
        dayLogDao.insertOrUpdate(entity)

        // Sync to cloud
        coroutineScope.launch {
            firestoreManager.saveDayRecord(
                DayRecord(
                    date = date,
                    status = DayStatus.CLEAN,
                    note = note,
                    urgeLevel = urgeLevel
                )
            )
        }
    }

    suspend fun markDayRelapse(
        date: String,
        note: String = "",
        triggerReason: String = "",
        assignedPenalty: Punishment? = null
    ) {
        val penaltyTitle = assignedPenalty?.title ?: "50 Flexões Imediatas"
        val dayEntity = DayLogEntity(
            date = date,
            status = DayStatus.RELAPSE.name,
            note = note,
            triggerReason = triggerReason,
            penaltyAssigned = penaltyTitle,
            penaltyCompleted = false,
            updatedAt = System.currentTimeMillis()
        )
        dayLogDao.insertOrUpdate(dayEntity)

        // Assign penalty to punishment list
        val penaltyToInsert = assignedPenalty ?: DefaultPunishments.TEMPLATES.first()
        val newId = UUID.randomUUID().toString()
        val penaltyEntity = PunishmentEntity(
            id = newId,
            title = penaltyToInsert.title,
            description = penaltyToInsert.description,
            category = penaltyToInsert.category,
            intensity = penaltyToInsert.intensity,
            isCompleted = false,
            dateAssigned = date,
            completedAt = null
        )
        punishmentDao.insert(penaltyEntity)

        // Cloud sync
        coroutineScope.launch {
            val record = DayRecord(
                date = date,
                status = DayStatus.RELAPSE,
                note = note,
                triggerReason = triggerReason,
                penaltyAssigned = penaltyTitle,
                penaltyCompleted = false
            )
            firestoreManager.saveDayRecord(record)
            firestoreManager.savePunishment(
                Punishment(
                    id = newId,
                    title = penaltyToInsert.title,
                    description = penaltyToInsert.description,
                    category = penaltyToInsert.category,
                    intensity = penaltyToInsert.intensity,
                    isCompleted = false,
                    dateAssigned = date
                )
            )
        }
    }

    suspend fun updateDayRecord(
        date: String,
        status: DayStatus,
        note: String,
        urgeLevel: Int,
        triggerReason: String
    ) {
        if (status == DayStatus.UNMARKED) {
            dayLogDao.deleteByDate(date)
        } else {
            val entity = DayLogEntity(
                date = date,
                status = status.name,
                note = note,
                urgeLevel = urgeLevel,
                triggerReason = triggerReason,
                updatedAt = System.currentTimeMillis()
            )
            dayLogDao.insertOrUpdate(entity)
            coroutineScope.launch {
                firestoreManager.saveDayRecord(
                    DayRecord(
                        date = date,
                        status = status,
                        note = note,
                        urgeLevel = urgeLevel,
                        triggerReason = triggerReason
                    )
                )
            }
        }
    }

    suspend fun completePunishment(id: String, completed: Boolean) {
        punishmentDao.markCompleted(
            id = id,
            completed = completed,
            completedAt = if (completed) System.currentTimeMillis() else null
        )
    }

    suspend fun addCustomPunishment(
        title: String,
        description: String,
        category: String,
        intensity: String
    ) {
        val id = UUID.randomUUID().toString()
        val date = DateUtils.getTodayString()
        val entity = PunishmentEntity(
            id = id,
            title = title,
            description = description,
            category = category,
            intensity = intensity,
            isCompleted = false,
            dateAssigned = date,
            completedAt = null
        )
        punishmentDao.insert(entity)
        coroutineScope.launch {
            firestoreManager.savePunishment(
                Punishment(
                    id = id,
                    title = title,
                    description = description,
                    category = category,
                    intensity = intensity,
                    isCompleted = false,
                    dateAssigned = date
                )
            )
        }
    }

    suspend fun deletePunishment(id: String) {
        punishmentDao.delete(id)
        coroutineScope.launch {
            firestoreManager.deletePunishment(id)
        }
    }

    suspend fun updateDailyMissions(missions: DailyMissions) {
        val current = _dailyMissionsFlow.value.toMutableMap()
        current[missions.date] = missions
        _dailyMissionsFlow.value = current

        coroutineScope.launch {
            firestoreManager.saveDailyMissions(missions)
        }
    }

    suspend fun addJournalEntry(mood: String, energyLevel: Int, reflection: String, gratitude: String) {
        val entry = JournalEntry(
            id = UUID.randomUUID().toString(),
            userId = firestoreManager.currentUserId() ?: "",
            date = DateUtils.getTodayString(),
            mood = mood,
            energyLevel = energyLevel,
            reflection = reflection,
            gratitude = gratitude,
            createdAt = System.currentTimeMillis()
        )
        val current = _journalEntriesFlow.value.toMutableList()
        current.add(0, entry)
        _journalEntriesFlow.value = current

        coroutineScope.launch {
            firestoreManager.saveJournalEntry(entry)
        }
    }

    fun getUserStatsFlow(): Flow<UserStats> {
        return combine(
            getAllDayRecords(),
            getPendingPunishmentsCount(),
            getAllPunishments(),
            _dailyMissionsFlow,
            _journalEntriesFlow
        ) { records, pendingCount, allPunishments, missionsMap, journalList ->
            calculateStats(records, pendingCount, allPunishments, missionsMap, journalList)
        }
    }

    private fun calculateStats(
        records: Map<String, DayRecord>,
        pendingCount: Int,
        allPunishments: List<Punishment>,
        missionsMap: Map<String, DailyMissions>,
        journalList: List<JournalEntry>
    ): UserStats {
        val today = DateUtils.getTodayString()

        var streak = 0
        val todayRecord = records[today]

        if (todayRecord?.status == DayStatus.RELAPSE) {
            streak = 0
        } else {
            var checkDate = if (todayRecord?.status == DayStatus.CLEAN) {
                streak++
                DateUtils.getPreviousDay(today)
            } else {
                DateUtils.getPreviousDay(today)
            }

            while (true) {
                val rec = records[checkDate]
                if (rec != null && rec.status == DayStatus.CLEAN) {
                    streak++
                    checkDate = DateUtils.getPreviousDay(checkDate)
                } else {
                    break
                }
            }
        }

        var totalClean = 0
        var totalRelapses = 0
        var earliestDate = today

        records.values.forEach { rec ->
            if (rec.status == DayStatus.CLEAN) totalClean++
            if (rec.status == DayStatus.RELAPSE) totalRelapses++
            if (rec.date < earliestDate) earliestDate = rec.date
        }

        val totalTrackedDays = maxOf(1, DateUtils.getDaysDifference(earliestDate, today) + 1)
        val unmarkedPastDays = maxOf(0, totalTrackedDays - (totalClean + totalRelapses))
        val totalEvaluated = maxOf(1, totalClean + totalRelapses + unmarkedPastDays)
        val successRate = ((totalClean.toFloat() / totalEvaluated) * 100).toInt()

        var bestStreak = streak
        var currentRunningStreak = 0
        var scanDate = earliestDate
        while (scanDate <= today) {
            val rec = records[scanDate]
            if (rec != null && rec.status == DayStatus.CLEAN) {
                currentRunningStreak++
                if (currentRunningStreak > bestStreak) {
                    bestStreak = currentRunningStreak
                }
            } else if (scanDate != today) {
                currentRunningStreak = 0
            }
            scanDate = DateUtils.getNextDay(scanDate)
        }

        val allMilestones = MilestoneData.MILESTONES
        val nextMilestone = allMilestones.firstOrNull { it.days > streak } ?: allMilestones.last()
        val prevMilestoneDays = allMilestones.lastOrNull { it.days <= streak }?.days ?: 0
        val daysRemaining = maxOf(0, nextMilestone.days - streak)

        val milestoneSpan = (nextMilestone.days - prevMilestoneDays).coerceAtLeast(1)
        val progressInMilestone = (streak - prevMilestoneDays).coerceAtLeast(0).toFloat() / milestoneSpan
        val progressToNext = progressInMilestone.coerceIn(0f, 1f)
        val hoursSaved = totalClean * 1.5f

        // RPG Experience Points (XP) Calculation:
        // +100 XP per clean day
        // +50 XP per completed punishment
        // +40 XP per completed daily mission set
        // +30 XP per journal entry
        val completedPunishmentsCount = allPunishments.count { it.isCompleted }
        val completedMissionsCount = missionsMap.values.count { it.isAllCompleted }
        val journalCount = journalList.size

        val totalXp = (totalClean * 100) +
                (completedPunishmentsCount * 50) +
                (completedMissionsCount * 40) +
                (journalCount * 30)

        val rank = RankSystem.getRankForXp(totalXp)
        val rankSpan = (rank.maxXp - rank.minXp).coerceAtLeast(1)
        val xpProgressInRank = ((totalXp - rank.minXp).toFloat() / rankSpan).coerceIn(0f, 1f)

        val missionsToday = missionsMap[today] ?: DailyMissions(today)

        // Sync user profile stats to cloud in background
        coroutineScope.launch {
            firestoreManager.syncUserProfile(
                streak = streak,
                bestStreak = maxOf(streak, bestStreak),
                xp = totalXp,
                rank = rank.title
            )
        }

        return UserStats(
            currentStreak = streak,
            bestStreak = maxOf(streak, bestStreak),
            totalCleanDays = totalClean,
            totalRelapses = totalRelapses,
            totalUnmarkedFalls = unmarkedPastDays,
            totalTrackedDays = totalTrackedDays,
            successRatePercent = successRate,
            hoursSaved = hoursSaved,
            nextMilestone = nextMilestone,
            daysRemainingToNextMilestone = daysRemaining,
            progressToNextMilestone = progressToNext,
            pendingPenaltiesCount = pendingCount,
            xp = totalXp,
            rank = rank,
            xpProgressInRank = xpProgressInRank,
            missionsToday = missionsToday
        )
    }

    suspend fun seedInitialDataIfEmpty() {
        val today = DateUtils.getTodayString()
        val existingToday = dayLogDao.getLogByDate(today)
        val allLogs = dayLogDao.getLogByDate(DateUtils.getPreviousDay(today))
        if (existingToday == null && allLogs == null) {
            var curr = DateUtils.getPreviousDay(today)
            for (i in 1..5) {
                dayLogDao.insertOrUpdate(
                    DayLogEntity(
                        date = curr,
                        status = DayStatus.CLEAN.name,
                        note = "Dia vitorioso! Treino e disciplina mental mantidos.",
                        urgeLevel = if (i % 2 == 0) 1 else 0,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                curr = DateUtils.getPreviousDay(curr)
            }
        }
    }
}
