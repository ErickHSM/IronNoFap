package com.example.model

data class FortitudeRank(
    val level: Int,
    val title: String,
    val minXp: Int,
    val maxXp: Int,
    val badgeName: String,
    val perkDescription: String
)

object RankSystem {
    val RANKS = listOf(
        FortitudeRank(1, "Recruta da Vontade", 0, 300, "Iniciante", "Primeiros passos no controle dos impulsos."),
        FortitudeRank(2, "Soldado de Ferro", 300, 700, "Resistência", "Quebrando o ciclo automático do cérebro."),
        FortitudeRank(3, "Sentinela da Mente", 700, 1400, "Foco", "Clareza mental e córtex pré-frontal fortalecido."),
        FortitudeRank(4, "Guerreiro Estóico", 1400, 2500, "Disciplina", "Dopamina recalibrada para esforço real."),
        FortitudeRank(5, "Mestre do Autodomínio", 2500, 4500, "Fortitude", "Controle absoluto sobre emoções e tentações."),
        FortitudeRank(6, "Titã Inabalável", 4500, 10000, "Lenda", "Mente forjada em aço. Domínio total da vida.")
    )

    fun getRankForXp(xp: Int): FortitudeRank {
        return RANKS.lastOrNull { xp >= it.minXp } ?: RANKS.first()
    }
}

data class DailyMissions(
    val date: String,
    val workoutDone: Boolean = false,
    val coldShowerDone: Boolean = false,
    val screenDetoxDone: Boolean = false
) {
    val completedCount: Int
        get() = (if (workoutDone) 1 else 0) + (if (coldShowerDone) 1 else 0) + (if (screenDetoxDone) 1 else 0)
    val isAllCompleted: Boolean
        get() = completedCount == 3
}

data class JournalEntry(
    val id: String = "",
    val userId: String = "",
    val date: String = "",
    val mood: String = "Focado",
    val energyLevel: Int = 3,
    val reflection: String = "",
    val gratitude: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
