package com.example.model

enum class DayStatus {
    CLEAN,     // Dias sem pornografia - Verde vibrante
    RELAPSE,   // Recaída confirmada - Vermelho alerta
    UNMARKED,  // Não marcado no passado (considerado queda conforme a regra do usuário)
    FUTURE     // Dias futuros ainda não alcançados
}

data class DayRecord(
    val date: String, // Formato "yyyy-MM-dd"
    val status: DayStatus,
    val note: String = "",
    val urgeLevel: Int = 0, // 0: Nenhuma, 1: Leve, 2: Média, 3: Intensa
    val triggerReason: String = "",
    val penaltyAssigned: String = "",
    val penaltyCompleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
