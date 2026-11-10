package com.example.model

data class Milestone(
    val days: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val brainBenefit: String,
    val isReached: Boolean = false
)

object MilestoneData {
    val MILESTONES = listOf(
        Milestone(
            days = 1,
            title = "O Primeiro Passo",
            subtitle = "24 Horas de Domínio",
            description = "A decisão mais importante é o primeiro dia. Você cortou o ciclo passivo e retomou o controle da sua consciência.",
            brainBenefit = "Interrupção do piloto automático neural."
        ),
        Milestone(
            days = 3,
            title = "A Primeira Barreira",
            subtitle = "72 Horas de Resistência",
            description = "O pico inicial de impulsividade começa a diminuir. Sua mente tenta negociar, mas sua vontade permanece firme.",
            brainBenefit = "Redução da hiperestimulação sensorial."
        ),
        Milestone(
            days = 7,
            title = "Uma Semana de Glória",
            subtitle = "7 Dias Ininterruptos",
            description = "Estudos apontam um aumento de receptores androgênicos e recuperação da energia vital e disposição diária.",
            brainBenefit = "Aumento de energia física e foco nos olhos."
        ),
        Milestone(
            days = 14,
            title = "A Névoa se Dissipa",
            subtitle = "2 Semanas de Clareza",
            description = "A névoa mental (brain fog) reduz drasticamente. Pensamento mais rápido, memória e menor ansiedade social.",
            brainBenefit = "Restauração do córtex pré-frontal e foco sustentado."
        ),
        Milestone(
            days = 21,
            title = "O Novo Alicerce",
            subtitle = "3 Semanas de Reestruturação",
            description = "O período clássico de quebra de hábito compulsivo. Sua rotina diária não depende mais do estímulo artificial.",
            brainBenefit = "Enfraquecimento dos caminhos neurais de gatilho."
        ),
        Milestone(
            days = 30,
            title = "Um Mês de Ferro",
            subtitle = "30 Dias de Fortitude",
            description = "Um marco monumental. Apenas uma minoria alcança 30 dias. Sua autoconfiança e respeito próprio são perceptíveis.",
            brainBenefit = "Ressensibilização dos receptores de dopamina D2."
        ),
        Milestone(
            days = 60,
            title = "A Grande Transformação",
            subtitle = "60 Dias de Sobriedade",
            description = "Mudança no olhar para a vida e relacionamentos. Pessoas reais tornam-se atraentes e a motivação intrínseca dispara.",
            brainBenefit = "Plasticidade neural profunda e estabilidade emocional."
        ),
        Milestone(
            days = 90,
            title = "O Grande Reboot",
            subtitle = "90 Dias de Renascimento",
            description = "O padrão ouro da neurociência para reinicialização do sistema de recompensa. Você venceu a dependência química e comportamental.",
            brainBenefit = "Cérebro recalibrado para recompensas normais da vida."
        ),
        Milestone(
            days = 180,
            title = "Meio Ano de Domínio",
            subtitle = "180 Dias Inquebráveis",
            description = "Não é mais um desafio de resistência: é o seu estilo de vida definitivo. Você é dono de si mesmo.",
            brainBenefit = "Fortaleza mental estabelecida e autocontrole reflexivo."
        ),
        Milestone(
            days = 365,
            title = "Um Ano de Liberdade",
            subtitle = "365 Dias Vitoriosos",
            description = "Liberdade plena. Você transformou um ano inteiro de energia desperdiçada em conquistas reais.",
            brainBenefit = "Transformação neural completa e caráter forjado."
        )
    )
}

data class UserStats(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCleanDays: Int = 0,
    val totalRelapses: Int = 0,
    val totalUnmarkedFalls: Int = 0,
    val totalTrackedDays: Int = 0,
    val successRatePercent: Int = 100,
    val hoursSaved: Float = 0f,
    val nextMilestone: Milestone = MilestoneData.MILESTONES[0],
    val daysRemainingToNextMilestone: Int = 1,
    val progressToNextMilestone: Float = 0f,
    val pendingPenaltiesCount: Int = 0,
    val xp: Int = 0,
    val rank: FortitudeRank = RankSystem.RANKS[0],
    val xpProgressInRank: Float = 0f,
    val missionsToday: DailyMissions = DailyMissions("")
)
