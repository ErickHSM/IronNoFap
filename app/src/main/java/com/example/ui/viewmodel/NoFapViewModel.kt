package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.AuthManager
import com.example.data.firestore.FirestoreManager
import com.example.data.local.AppDatabase
import com.example.data.repository.NoFapRepository
import com.example.model.DailyMissions
import com.example.model.DayRecord
import com.example.model.DayStatus
import com.example.model.JournalEntry
import com.example.model.Punishment
import com.example.model.UserStats
import com.example.util.DateUtils
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class NoFapUiState(
    val records: Map<String, DayRecord> = emptyMap(),
    val stats: UserStats = UserStats(),
    val punishments: List<Punishment> = emptyList(),
    val journalEntries: List<JournalEntry> = emptyList(),
    val dailyMissions: DailyMissions = DailyMissions(DateUtils.getTodayString()),
    val selectedDate: String = DateUtils.getTodayString(),
    val selectedRecord: DayRecord? = null,
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val showRelapseDialog: Boolean = false,
    val showDayDetailSheet: Boolean = false,
    val showAddPunishmentDialog: Boolean = false,
    val showEmergencyMode: Boolean = false,
    val showJournalDialog: Boolean = false,
    val showAccountDialog: Boolean = false,
    val currentUser: FirebaseUser? = AuthManager.currentUser,
    val toastMessage: String? = null
)

class NoFapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoFapRepository
    val firestoreManager: FirestoreManager

    private val _uiState = MutableStateFlow(NoFapUiState())
    val uiState: StateFlow<NoFapUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        firestoreManager = FirestoreManager(application)
        repository = NoFapRepository(db.dayLogDao(), db.punishmentDao(), firestoreManager)

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        viewModelScope.launch {
            repository.getAllDayRecords().collectLatest { recordsMap ->
                _uiState.update { state ->
                    val selRecord = recordsMap[state.selectedDate] ?: DayRecord(
                        date = state.selectedDate,
                        status = if (DateUtils.isFuture(state.selectedDate)) DayStatus.FUTURE
                        else if (DateUtils.isPast(state.selectedDate)) DayStatus.UNMARKED
                        else DayStatus.UNMARKED
                    )
                    state.copy(records = recordsMap, selectedRecord = selRecord)
                }
            }
        }

        viewModelScope.launch {
            repository.getUserStatsFlow().collectLatest { stats ->
                _uiState.update { it.copy(stats = stats) }
            }
        }

        viewModelScope.launch {
            repository.getAllPunishments().collectLatest { punishmentsList ->
                _uiState.update { it.copy(punishments = punishmentsList) }
            }
        }

        viewModelScope.launch {
            val today = DateUtils.getTodayString()
            repository.getDailyMissions(today).collectLatest { missions ->
                _uiState.update { it.copy(dailyMissions = missions) }
            }
        }

        viewModelScope.launch {
            repository.getJournalEntries().collectLatest { journalList ->
                _uiState.update { it.copy(journalEntries = journalList) }
            }
        }
    }

    fun onUserSignedIn(user: FirebaseUser) {
        _uiState.update { it.copy(currentUser = user, toastMessage = "Conectado como ${user.displayName ?: user.email}! Dados salvos na nuvem.") }
    }

    fun onUserSignedOut() {
        _uiState.update { it.copy(currentUser = null, toastMessage = "Desconectado. Dados permanecem salvos localmente.") }
    }

    fun markTodayClean(note: String = "", urgeLevel: Int = 0) {
        viewModelScope.launch {
            val today = DateUtils.getTodayString()
            repository.markDayClean(today, note, urgeLevel)
            _uiState.update { it.copy(toastMessage = "Vitória registrada! +100 XP adicionados à sua patente.") }
        }
    }

    fun markTodayRelapse(note: String, triggerReason: String, penalty: Punishment?) {
        viewModelScope.launch {
            val today = DateUtils.getTodayString()
            repository.markDayRelapse(today, note, triggerReason, penalty)
            _uiState.update {
                it.copy(
                    showRelapseDialog = false,
                    toastMessage = "Recaída registrada. Punição atribuída. Cumpra a disciplina para ganhar +50 XP de redenção!"
                )
            }
        }
    }

    fun selectDate(date: String) {
        _uiState.update { state ->
            val record = state.records[date] ?: DayRecord(
                date = date,
                status = if (DateUtils.isFuture(date)) DayStatus.FUTURE
                else if (DateUtils.isPast(date)) DayStatus.UNMARKED
                else DayStatus.UNMARKED
            )
            state.copy(
                selectedDate = date,
                selectedRecord = record,
                showDayDetailSheet = true
            )
        }
    }

    fun updateSelectedDateRecord(
        date: String,
        status: DayStatus,
        note: String,
        urgeLevel: Int,
        trigger: String
    ) {
        viewModelScope.launch {
            repository.updateDayRecord(date, status, note, urgeLevel, trigger)
            _uiState.update { it.copy(showDayDetailSheet = false, toastMessage = "Registro atualizado com sucesso.") }
        }
    }

    fun toggleWorkoutMission(checked: Boolean) {
        viewModelScope.launch {
            val current = _uiState.value.dailyMissions
            val updated = current.copy(workoutDone = checked)
            repository.updateDailyMissions(updated)
            if (checked) _uiState.update { it.copy(toastMessage = "Treino Físico concluído! Corpo fortalecido.") }
        }
    }

    fun toggleColdShowerMission(checked: Boolean) {
        viewModelScope.launch {
            val current = _uiState.value.dailyMissions
            val updated = current.copy(coldShowerDone = checked)
            repository.updateDailyMissions(updated)
            if (checked) _uiState.update { it.copy(toastMessage = "Choque Térmico vencido! Dopamina resetada.") }
        }
    }

    fun toggleScreenDetoxMission(checked: Boolean) {
        viewModelScope.launch {
            val current = _uiState.value.dailyMissions
            val updated = current.copy(screenDetoxDone = checked)
            repository.updateDailyMissions(updated)
            if (checked) _uiState.update { it.copy(toastMessage = "Detox de Telas Noturno ativado! Sono protegido.") }
        }
    }

    fun addJournalEntry(mood: String, energyLevel: Int, reflection: String, gratitude: String) {
        viewModelScope.launch {
            repository.addJournalEntry(mood, energyLevel, reflection, gratitude)
            _uiState.update { it.copy(showJournalDialog = false, toastMessage = "Reflexão salva no Diário de Bordo! +30 XP.") }
        }
    }

    fun dismissDayDetailSheet() {
        _uiState.update { it.copy(showDayDetailSheet = false) }
    }

    fun openRelapseDialog() {
        _uiState.update { it.copy(showRelapseDialog = true) }
    }

    fun dismissRelapseDialog() {
        _uiState.update { it.copy(showRelapseDialog = false) }
    }

    fun openAddPunishmentDialog() {
        _uiState.update { it.copy(showAddPunishmentDialog = true) }
    }

    fun dismissAddPunishmentDialog() {
        _uiState.update { it.copy(showAddPunishmentDialog = false) }
    }

    fun toggleEmergencyMode(show: Boolean) {
        _uiState.update { it.copy(showEmergencyMode = show) }
    }

    fun openJournalDialog() {
        _uiState.update { it.copy(showJournalDialog = true) }
    }

    fun dismissJournalDialog() {
        _uiState.update { it.copy(showJournalDialog = false) }
    }

    fun openAccountDialog() {
        _uiState.update { it.copy(showAccountDialog = true) }
    }

    fun dismissAccountDialog() {
        _uiState.update { it.copy(showAccountDialog = false) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun changeMonth(delta: Int) {
        _uiState.update { state ->
            var newMonth = state.selectedMonth + delta
            var newYear = state.selectedYear
            if (newMonth > 11) {
                newMonth = 0
                newYear++
            } else if (newMonth < 0) {
                newMonth = 11
                newYear--
            }
            state.copy(selectedMonth = newMonth, selectedYear = newYear)
        }
    }

    fun resetToCurrentMonth() {
        val cal = Calendar.getInstance()
        _uiState.update {
            it.copy(
                selectedMonth = cal.get(Calendar.MONTH),
                selectedYear = cal.get(Calendar.YEAR)
            )
        }
    }

    fun completePunishment(id: String, completed: Boolean) {
        viewModelScope.launch {
            repository.completePunishment(id, completed)
            if (completed) {
                _uiState.update { it.copy(toastMessage = "Disciplina cumprida! +50 XP de honra e redenção creditados.") }
            }
        }
    }

    fun addCustomPunishment(title: String, desc: String, category: String, intensity: String) {
        viewModelScope.launch {
            repository.addCustomPunishment(title, desc, category, intensity)
            _uiState.update { it.copy(showAddPunishmentDialog = false, toastMessage = "Nova punição cadastrada no sistema.") }
        }
    }

    fun deletePunishment(id: String) {
        viewModelScope.launch {
            repository.deletePunishment(id)
        }
    }
}
