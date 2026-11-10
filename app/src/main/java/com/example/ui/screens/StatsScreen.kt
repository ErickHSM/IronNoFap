package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DayRecord
import com.example.model.DayStatus
import com.example.model.MilestoneData
import com.example.model.UserStats
import com.example.ui.components.GlassCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldBadge
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils

@Composable
fun StatsScreen(
    stats: UserStats,
    records: Map<String, DayRecord>
) {
    val milestones = MilestoneData.MILESTONES
    val today = DateUtils.getTodayString()

    val last7Days = (6 downTo 0).map { offset ->
        DateUtils.addDays(today, -offset)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("stats_screen")
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BlueAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QueryStats,
                    contentDescription = null,
                    tint = BlueAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Estatísticas & Patente",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Métricas reais da sua transformação neural",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // RPG Rank Overview Banner
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            glowColor = Color(0x44FBBF24)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GoldBadge.copy(alpha = 0.2f))
                        .border(1.5.dp, GoldBadge, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = null,
                        tint = GoldBadge,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Patente: ${stats.rank.title}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = GoldBadge
                        )
                    )
                    Text(
                        text = "${stats.xp} XP Acumulados • Nível ${stats.rank.level}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2x2 Primary Stats Grid using GlassCards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatGlassCard(
                title = "Sequência Atual",
                value = "${stats.currentStreak} dias",
                subtitle = "Dias consecutivos limpo",
                accentColor = EmeraldPrimary,
                modifier = Modifier.weight(1f)
            )
            StatGlassCard(
                title = "Recorde Histórico",
                value = "${stats.bestStreak} dias",
                subtitle = "Sua melhor marca",
                accentColor = GoldBadge,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatGlassCard(
                title = "Taxa de Sucesso",
                value = "${stats.successRatePercent}%",
                subtitle = "${stats.totalCleanDays} dias vencidos",
                accentColor = if (stats.successRatePercent >= 70) EmeraldPrimary else AmberWarning,
                modifier = Modifier.weight(1f)
            )
            StatGlassCard(
                title = "Tempo Salvo",
                value = "~${stats.hoursSaved.toInt()}h",
                subtitle = "Longe de telas tóxicas",
                accentColor = BlueAccent,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Last 7 Days Consistency Strip
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Consistência dos Últimos 7 Dias",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    last7Days.forEach { dateStr ->
                        val rec = records[dateStr]
                        val isToday = DateUtils.isToday(dateStr)
                        val status = rec?.status
                        val isClean = status == DayStatus.CLEAN
                        val isRelapse = status == DayStatus.RELAPSE

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = DateUtils.formatShortDisplayDate(dateStr),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isToday) EmeraldPrimary else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isClean -> EmeraldPrimary
                                            isRelapse -> CrimsonAlert
                                            isToday -> DarkOutline
                                            else -> Color(0xFF2B1D20)
                                        }
                                    )
                                    .border(
                                        if (isToday) 2.dp else 0.dp,
                                        if (isToday) Color.White else Color.Transparent,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isClean -> Icon(Icons.Default.Check, null, tint = Color(0xFF003816), modifier = Modifier.size(16.dp))
                                    isRelapse -> Text("✕", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    isToday -> Text("•", color = EmeraldPrimary, fontSize = 16.sp)
                                    else -> Text("—", color = CrimsonAlert, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Neurobiological Reboot Roadmap
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MilitaryTech,
                    contentDescription = null,
                    tint = GoldBadge,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Marcos do Grande Reboot",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
            Text(
                text = "${stats.currentStreak} dias ativos",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            milestones.forEach { milestone ->
                val isUnlocked = stats.currentStreak >= milestone.days
                val isNext = stats.nextMilestone.days == milestone.days && !isUnlocked

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    borderColor = if (isUnlocked) EmeraldPrimary else if (isNext) BlueAccent else null,
                    glowColor = if (isUnlocked) Color(0x3300E676) else Color(0x10FFFFFF)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                        .background(
                                            if (isUnlocked) EmeraldPrimary
                                            else if (isNext) BlueAccent.copy(alpha = 0.2f)
                                            else DarkOutline
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isUnlocked) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Desbloqueado",
                                            tint = Color(0xFF003816),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else if (isNext) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = "Próximo",
                                            tint = BlueAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Bloqueado",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "${milestone.days} Dias: ${milestone.title}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUnlocked) Color.White else TextPrimary
                                        )
                                    )
                                    Text(
                                        text = milestone.subtitle,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isUnlocked) EmeraldPrimary else TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            if (isNext) {
                                Text(
                                    text = "Faltam ${milestone.days - stats.currentStreak} dias",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BlueAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = milestone.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = if (isUnlocked) EmeraldPrimary else GoldBadge,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = milestone.brainBenefit,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isUnlocked) EmeraldPrimary else GoldBadge,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatGlassCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        borderColor = accentColor.copy(alpha = 0.5f),
        glowColor = accentColor.copy(alpha = 0.2f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = accentColor,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }
    }
}
