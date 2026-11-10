package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyMissions
import com.example.model.DayRecord
import com.example.model.DayStatus
import com.example.model.UserStats
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldBadge
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.google.firebase.auth.FirebaseUser

@Composable
fun DashboardScreen(
    stats: UserStats,
    todayRecord: DayRecord?,
    dailyMissions: DailyMissions,
    currentUser: FirebaseUser?,
    onMarkTodayClean: () -> Unit,
    onOpenRelapseDialog: () -> Unit,
    onOpenEmergencySOS: () -> Unit,
    onOpenAccountDialog: () -> Unit,
    onOpenJournalDialog: () -> Unit,
    onToggleWorkout: (Boolean) -> Unit,
    onToggleColdShower: (Boolean) -> Unit,
    onToggleScreenDetox: (Boolean) -> Unit,
    onNavigateToPunishments: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    val isCleanToday = todayRecord?.status == DayStatus.CLEAN
    val isRelapseToday = todayRecord?.status == DayStatus.RELAPSE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("dashboard_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: Account / Cloud Sync + App Title + SOS Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onOpenAccountDialog() }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceCard)
                        .border(
                            1.5.dp,
                            if (currentUser != null) EmeraldPrimary else DarkOutline,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (currentUser != null) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                        contentDescription = "Conta",
                        tint = if (currentUser != null) EmeraldPrimary else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "IRONWILL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (currentUser != null) EmeraldPrimary else AmberWarning)
                        )
                    }
                    Text(
                        text = if (currentUser != null) "Nuvem Sincronizada" else "Modo Offline (Toque p/ Conectar)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (currentUser != null) EmeraldPrimary else TextSecondary,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Glowing SOS Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(CrimsonAlert.copy(alpha = 0.15f))
                    .border(1.5.dp, CrimsonAlert, RoundedCornerShape(14.dp))
                    .clickable { onOpenEmergencySOS() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("sos_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "SOS",
                        tint = CrimsonAlert,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SOS",
                        color = CrimsonAlert,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Hero Streak Card with Multi-layered Neon Lighting Aura
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            glowColor = Color(0x6600E676)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Streak badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(EmeraldContainer)
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stats.rank.title.uppercase(),
                        color = EmeraldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High-Voltage Neon Lightning Glow Ring
                Box(
                    modifier = Modifier.size(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer radial lightning aura
                    Box(
                        modifier = Modifier
                            .size(185.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        EmeraldPrimary.copy(alpha = 0.4f),
                                        EmeraldPrimary.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Core ring with glowing border
                    Box(
                        modifier = Modifier
                            .size(155.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF14171D))
                            .border(
                                3.dp,
                                Brush.linearGradient(
                                    listOf(
                                        EmeraldPrimary,
                                        Color(0xFF7AFAB0),
                                        EmeraldPrimary
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${stats.currentStreak}",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldPrimary,
                                    fontSize = 58.sp
                                )
                            )
                            Text(
                                text = if (stats.currentStreak == 1) "DIA LIMPO" else "DIAS LIMPO",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 2.sp,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = when {
                        isCleanToday -> "✓ Hoje está marcado como Vencido!"
                        isRelapseToday -> "⚠ Recaída registrada hoje. Cumpra a punição."
                        else -> "Hoje ainda em andamento. Mantenha a vigilância!"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isCleanToday) EmeraldPrimary else if (isRelapseToday) CrimsonAlert else TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mini stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Recorde", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp))
                        Text(
                            text = "${stats.bestStreak}d",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkOutline))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "XP Total", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp))
                        Text(
                            text = "${stats.xp} XP",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = GoldBadge)
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(DarkOutline))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Tempo Salvo", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp))
                        Text(
                            text = "${stats.hoursSaved.toInt()}h",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BlueAccent)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System 1: Patente RPG & Experiência (Rank Card)
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            glowColor = Color(0x33FBBF24)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GoldBadge.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = GoldBadge,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Nível ${stats.rank.level}: ${stats.rank.title}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = stats.rank.perkDescription,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Text(
                        text = "${stats.xp} XP",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = GoldBadge
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { stats.xpProgressInRank },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GoldBadge,
                    trackColor = DarkOutline,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Próxima Patente aos ${stats.rank.maxXp} XP (+100 XP por dia limpo)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System 2: Missões Diárias de Disciplina (Daily Habits)
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            glowColor = Color(0x3338BDF8)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = BlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Missões Diárias de Disciplina",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BlueAccent.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${dailyMissions.completedCount}/3 Concluídas",
                            color = BlueAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Cumpra os 3 pilares para canalizar a energia da dopamina:",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mission 1: Treino Físico
                MissionItem(
                    title = "Treino Físico Intenso (20+ min)",
                    subtitle = "Gaste o cortisol e fortaleça seu corpo.",
                    isChecked = dailyMissions.workoutDone,
                    onToggle = onToggleWorkout
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Mission 2: Choque Térmico
                MissionItem(
                    title = "Choque Térmico / Banho Frio (3+ min)",
                    subtitle = "Interrompa a fissura dopaminérgica instantaneamente.",
                    isChecked = dailyMissions.coldShowerDone,
                    onToggle = onToggleColdShower
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Mission 3: Detox de Telas
                MissionItem(
                    title = "Detox Noturno de Telas (1h antes de dormir)",
                    subtitle = "Sem celular na cama, elimine o gatilho da solidão.",
                    isChecked = dailyMissions.screenDetoxDone,
                    onToggle = onToggleScreenDetox
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System 3: Diário de Bordo Rápido
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenJournalDialog() },
            shape = RoundedCornerShape(18.dp),
            glowColor = Color(0x3360A5FA)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BlueAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = BlueAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Diário de Bordo & Mente",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Registre humor, energia e reflexão (+30 XP)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Active Penalties Alert Card
        AnimatedVisibility(visible = stats.pendingPenaltiesCount > 0) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToPunishments() },
                    shape = RoundedCornerShape(18.dp),
                    borderColor = CrimsonAlert,
                    backgroundColor = CrimsonContainer
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CrimsonAlert),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Você tem ${stats.pendingPenaltiesCount} Punição(ões) Ativa(s)!",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Pague a disciplina para recuperar sua honra e ganhar +50 XP.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Countdown to Next Milestone Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            glowColor = Color(0x3300E676)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CONTAGEM REGRESSIVA",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Text(
                        text = "Meta: ${stats.nextMilestone.days} Dias",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldBadge
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stats.nextMilestone.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = stats.nextMilestone.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (stats.daysRemainingToNextMilestone == 0) "Alcançado!"
                            else "Faltam ${stats.daysRemainingToNextMilestone} ${
                                if (stats.daysRemainingToNextMilestone == 1) "dia" else "dias"
                            }",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = EmeraldPrimary
                            )
                        )
                        Text(
                            text = "${(stats.progressToNextMilestone * 100).toInt()}% concluído",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { stats.progressToNextMilestone },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EmeraldPrimary,
                    trackColor = DarkOutline,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Benefício Neural: ${stats.nextMilestone.brainBenefit}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Daily Check-In Actions
        Text(
            text = "Registro de Hoje",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (isCleanToday) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                borderColor = EmeraldPrimary,
                backgroundColor = EmeraldContainer
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Hoje Marcado: Limpo!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Vitória salva na nuvem e no calendário.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onNavigateToCalendar,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldPrimary)
                    ) {
                        Text("Ver Calendário", fontSize = 11.sp)
                    }
                }
            }
        } else {
            // Big Action Button: Mark Clean
            Button(
                onClick = onMarkTodayClean,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("mark_today_clean_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color(0xFF003816)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF003816),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Venci Hoje (Limpo +100 XP)",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Report Relapse Button
            OutlinedButton(
                onClick = onOpenRelapseDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("report_relapse_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonAlert),
                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonAlert.copy(alpha = 0.5f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = CrimsonAlert,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tive uma Recaída (Ativar Punição)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun MissionItem(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceCard)
            .clickable { onToggle(!isChecked) }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onToggle(it) },
            colors = CheckboxDefaults.colors(
                checkedColor = EmeraldPrimary,
                uncheckedColor = TextSecondary,
                checkmarkColor = Color(0xFF003816)
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isChecked) EmeraldPrimary else TextPrimary,
                    fontSize = 13.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            )
        }
    }
}
