package com.example.model

data class Punishment(
    val id: String,
    val title: String,
    val description: String,
    val category: String = "Física", // Física, Mental, Detox Digital, Produtividade
    val intensity: String = "Moderada", // Leve, Moderada, Extrema
    val isCompleted: Boolean = false,
    val dateAssigned: String, // "yyyy-MM-dd"
    val completedAt: Long? = null
)

object DefaultPunishments {
    val TEMPLATES = listOf(
        Punishment(
            id = "template_pushups",
            title = "50 Flexões Imediatas",
            description = "Fazer 50 flexões (podem ser divididas em séries) para direcionar a energia e restaurar o foco físico.",
            category = "Física",
            intensity = "Moderada",
            dateAssigned = ""
        ),
        Punishment(
            id = "template_cold_shower",
            title = "Banho 100% Gelado (5 Minutos)",
            description = "Tomar um banho completamente gelado por no mínimo 5 minutos para interromper o fluxo de dopamina artificial.",
            category = "Física",
            intensity = "Extrema",
            dateAssigned = ""
        ),
        Punishment(
            id = "template_social_media_fast",
            title = "Detox Digital: 24h sem Redes Sociais",
            description = "Desinstalar ou não abrir Instagram, TikTok, Twitter e YouTube Shorts pelas próximas 24 horas.",
            category = "Detox Digital",
            intensity = "Extrema",
            dateAssigned = ""
        ),
        Punishment(
            id = "template_read_book",
            title = "Ler 20 Páginas de um Livro Físico",
            description = "Sentar em silêncio longe de telas e ler 20 páginas de um livro de desenvolvimento pessoal ou estudo.",
            category = "Mental",
            intensity = "Moderada",
            dateAssigned = ""
        ),
        Punishment(
            id = "template_run_walk",
            title = "Caminhada ou Corrida de 3 km",
            description = "Sair de casa sem fones ou estímulos e completar 3km em ritmo vigoroso.",
            category = "Física",
            intensity = "Moderada",
            dateAssigned = ""
        ),
        Punishment(
            id = "template_write_reflection",
            title = "Carta de Responsabilidade (15 Linhas)",
            description = "Escrever à mão em um caderno: por que caí, o que me fez fraco e qual a ação concreta para não repetir.",
            category = "Mental",
            intensity = "Leve",
            dateAssigned = ""
        ),
        Punishment(
            id = "template_clean_room",
            title = "Faxina Profunda no Quarto/Ambiente",
            description = "Organizar completamente seu quarto, trocar lençóis e eliminar qualquer desordem física ao seu redor.",
            category = "Produtividade",
            intensity = "Leve",
            dateAssigned = ""
        )
    )
}
