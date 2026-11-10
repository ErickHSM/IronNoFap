package com.example.data.firestore

import android.content.Context
import com.example.R
import com.example.model.DailyMissions
import com.example.model.DayRecord
import com.example.model.DayStatus
import com.example.model.JournalEntry
import com.example.model.Punishment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirestoreManager(private val context: Context) {

    // Invariant 3: Always initialize with R.string.firestore_database_id
    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance(context.getString(R.string.firestore_database_id))
    }

    private val auth get() = Firebase.auth

    fun currentUserId(): String? = auth.currentUser?.uid

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- User Profile Sync ---
    suspend fun syncUserProfile(
        streak: Int,
        bestStreak: Int,
        xp: Int,
        rank: String
    ) {
        val uid = currentUserId() ?: return
        val docRef = db.collection("users").document(uid)
        val user = auth.currentUser
        val displayName = user?.displayName ?: "Guerreiro de Ferro"
        val email = user?.email ?: ""

        val payload = mapOf(
            "userId" to uid,
            "displayName" to displayName,
            "email" to email,
            "currentStreak" to streak,
            "bestStreak" to bestStreak,
            "xp" to xp,
            "rank" to rank,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            awaitTask(docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
        }
    }

    // --- Day Records Sync ---
    suspend fun saveDayRecord(record: DayRecord) {
        val uid = currentUserId() ?: return
        val path = "users/$uid/day_records/${record.date}"
        val docRef = db.collection("users").document(uid).collection("day_records").document(record.date)

        val payload = mapOf(
            "userId" to uid,
            "date" to record.date,
            "status" to record.status.name,
            "note" to record.note,
            "urgeLevel" to record.urgeLevel,
            "triggerReason" to record.triggerReason,
            "penaltyAssigned" to record.penaltyAssigned,
            "penaltyCompleted" to record.penaltyCompleted,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            awaitTask(docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
        }
    }

    fun observeDayRecords(): Flow<Map<String, DayRecord>> = flow {
        val uid = currentUserId() ?: return@flow
        val path = "users/$uid/day_records"
        val collection = db.collection("users").document(uid).collection("day_records")

        val flow = collection.snapshots().map { snapshot ->
            snapshot.documents.associate { doc ->
                val date = doc.getString("date") ?: doc.id
                val statusStr = doc.getString("status") ?: "UNMARKED"
                val status = try { DayStatus.valueOf(statusStr) } catch (e: Exception) { DayStatus.UNMARKED }
                val note = doc.getString("note") ?: ""
                val urgeLevel = (doc.getLong("urgeLevel") ?: 0L).toInt()
                val triggerReason = doc.getString("triggerReason") ?: ""
                val penaltyAssigned = doc.getString("penaltyAssigned") ?: ""
                val penaltyCompleted = doc.getBoolean("penaltyCompleted") ?: false

                date to DayRecord(
                    date = date,
                    status = status,
                    note = note,
                    urgeLevel = urgeLevel,
                    triggerReason = triggerReason,
                    penaltyAssigned = penaltyAssigned,
                    penaltyCompleted = penaltyCompleted
                )
            }
        }.catch { error ->
            handleFirestoreError(error, OperationType.LIST, path)
        }

        emitAll(flow)
    }

    // --- Punishments Sync ---
    suspend fun savePunishment(punishment: Punishment) {
        val uid = currentUserId() ?: return
        val path = "users/$uid/punishments/${punishment.id}"
        val docRef = db.collection("users").document(uid).collection("punishments").document(punishment.id)

        val payload = mapOf(
            "userId" to uid,
            "id" to punishment.id,
            "title" to punishment.title,
            "description" to punishment.description,
            "category" to punishment.category,
            "intensity" to punishment.intensity,
            "isCompleted" to punishment.isCompleted,
            "dateAssigned" to punishment.dateAssigned,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            awaitTask(docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
        }
    }

    suspend fun deletePunishment(id: String) {
        val uid = currentUserId() ?: return
        val path = "users/$uid/punishments/$id"
        val docRef = db.collection("users").document(uid).collection("punishments").document(id)
        try {
            awaitTask(docRef.delete())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
        }
    }

    fun observePunishments(): Flow<List<Punishment>> = flow {
        val uid = currentUserId() ?: return@flow
        val path = "users/$uid/punishments"
        val collection = db.collection("users").document(uid).collection("punishments")

        val flow = collection.snapshots().map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val title = doc.getString("title") ?: return@mapNotNull null
                val description = doc.getString("description") ?: ""
                val category = doc.getString("category") ?: "Física"
                val intensity = doc.getString("intensity") ?: "Moderada"
                val isCompleted = doc.getBoolean("isCompleted") ?: false
                val dateAssigned = doc.getString("dateAssigned") ?: ""

                Punishment(
                    id = id,
                    title = title,
                    description = description,
                    category = category,
                    intensity = intensity,
                    isCompleted = isCompleted,
                    dateAssigned = dateAssigned
                )
            }
        }.catch { error ->
            handleFirestoreError(error, OperationType.LIST, path)
        }

        emitAll(flow)
    }

    // --- Daily Missions Sync ---
    suspend fun saveDailyMissions(missions: DailyMissions) {
        val uid = currentUserId() ?: return
        val path = "users/$uid/daily_missions/${missions.date}"
        val docRef = db.collection("users").document(uid).collection("daily_missions").document(missions.date)

        val payload = mapOf(
            "userId" to uid,
            "date" to missions.date,
            "workoutDone" to missions.workoutDone,
            "coldShowerDone" to missions.coldShowerDone,
            "screenDetoxDone" to missions.screenDetoxDone,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            awaitTask(docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
        }
    }

    fun observeDailyMissions(date: String): Flow<DailyMissions?> = flow {
        val uid = currentUserId() ?: return@flow
        val path = "users/$uid/daily_missions/$date"
        val docRef = db.collection("users").document(uid).collection("daily_missions").document(date)

        val flow = docRef.snapshots().map { doc ->
            if (doc.exists()) {
                DailyMissions(
                    date = date,
                    workoutDone = doc.getBoolean("workoutDone") ?: false,
                    coldShowerDone = doc.getBoolean("coldShowerDone") ?: false,
                    screenDetoxDone = doc.getBoolean("screenDetoxDone") ?: false
                )
            } else {
                DailyMissions(date)
            }
        }.catch { error ->
            handleFirestoreError(error, OperationType.GET, path)
        }

        emitAll(flow)
    }

    // --- Journal Entries Sync ---
    suspend fun saveJournalEntry(entry: JournalEntry) {
        val uid = currentUserId() ?: return
        val path = "users/$uid/journal_entries/${entry.id}"
        val docRef = db.collection("users").document(uid).collection("journal_entries").document(entry.id)

        val payload = mapOf(
            "userId" to uid,
            "id" to entry.id,
            "date" to entry.date,
            "mood" to entry.mood,
            "energyLevel" to entry.energyLevel,
            "reflection" to entry.reflection,
            "gratitude" to entry.gratitude,
            "createdAt" to FieldValue.serverTimestamp()
        )

        try {
            awaitTask(docRef.set(payload))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
        }
    }

    fun observeJournalEntries(): Flow<List<JournalEntry>> = flow {
        val uid = currentUserId() ?: return@flow
        val path = "users/$uid/journal_entries"
        val collection = db.collection("users").document(uid).collection("journal_entries")

        val flow = collection.snapshots().map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val date = doc.getString("date") ?: ""
                val mood = doc.getString("mood") ?: "Focado"
                val energyLevel = (doc.getLong("energyLevel") ?: 3L).toInt()
                val reflection = doc.getString("reflection") ?: ""
                val gratitude = doc.getString("gratitude") ?: ""

                JournalEntry(
                    id = id,
                    userId = uid,
                    date = date,
                    mood = mood,
                    energyLevel = energyLevel,
                    reflection = reflection,
                    gratitude = gratitude
                )
            }
        }.catch { error ->
            handleFirestoreError(error, OperationType.LIST, path)
        }

        emitAll(flow)
    }

    // Generic safe await helper
    private suspend fun <T> awaitTask(task: com.google.android.gms.tasks.Task<T>): T =
        suspendCancellableCoroutine { cont ->
            task.addOnSuccessListener { cont.resume(it) }
            task.addOnFailureListener { cont.resumeWithException(it) }
            task.addOnCanceledListener { cont.cancel() }
        }
}
