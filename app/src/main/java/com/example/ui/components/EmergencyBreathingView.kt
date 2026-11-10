package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun EmergencyBreathingDialog(
    onDismiss: () -> Unit
) {
    // 4-7-8 Breathing phases: 0 -> Inhale (4s), 1 -> Hold (7s), 2 -> Exhale (8s)
    var phase by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(4) }
    var isBreathingRunning by remember { mutableStateOf(true) }

    // 15-Minute Crisis Countdown Timer (900 seconds)
    var crisisSecondsRemaining by remember { mutableIntStateOf(15 * 60) }
    var isCrisisTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isBreathingRunning, phase) {
        if (!isBreathingRunning) return@LaunchedEffect

        val targetSeconds = when (phase) {
            0 -> 4
            1 -> 7
            else -> 8
        }
        secondsLeft = targetSeconds

        while (secondsLeft > 0) {
            delay(1000L)
            secondsLeft--
        }

        phase = (phase + 1) % 3
    }

    LaunchedEffect(isCrisisTimerRunning) {
        while (isCrisisTimerRunning && crisisSecondsRemaining > 0) {
            delay(1000L)
            crisisSecondsRemaining--
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = when (phase) {
            0 -> 1.35f
            1 -> 1.35f
            else -> 0.9f
        },
        animationSpec = tween(
            durationMillis = when (phase) {
                0 -> 4000
                1 -> 7000
                else -> 8000
            },
            easing = FastOutSlowInEasing
        ),
        label = "BreathingScale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("emergency_sos_dialog"),
            color = DarkBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CrimsonAlert.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = CrimsonAlert,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "MODO EMERGÊNCIA SOS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = CrimsonAlert,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Sair", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 15-Minute Crisis Wave Timer Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = CrimsonAlert
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = CrimsonAlert,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Temporizador da Onda Biológica",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                            }

                            IconButton(
                                onClick = { isCrisisTimerRunning = !isCrisisTimerRunning },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCrisisTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = CrimsonAlert
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val minutes = crisisSecondsRemaining / 60
                        val seconds = crisisSecondsRemaining % 60
                        val formattedTime = String.format("%02d:%02d", minutes, seconds)

                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Black,
                                color = CrimsonAlert,
                                letterSpacing = 2.sp
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        val progress = 1f - (crisisSecondsRemaining.toFloat() / (15 * 60))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CrimsonAlert,
                            trackColor = DarkOutline,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "A onda de dopamina dura no máximo 15 minutos. Resista até o cronômetro zerar e a vontade cessará.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Breathing circle guide
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .scale(animatedScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        when (phase) {
                                            0 -> EmeraldPrimary.copy(alpha = 0.35f)
                                            1 -> BlueAccent.copy(alpha = 0.35f)
                                            else -> Color(0xFF6366F1).copy(alpha = 0.3f)
                                        },
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceCard)
                            .border(
                                2.dp,
                                when (phase) {
                                    0 -> EmeraldPrimary
                                    1 -> BlueAccent
                                    else -> Color(0xFF818CF8)
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = when (phase) {
                                    0 -> "INSPIRE"
                                    1 -> "SEGURE"
                                    else -> "EXPIRE"
                                },
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = when (phase) {
                                        0 -> EmeraldPrimary
                                        1 -> BlueAccent
                                        else -> Color(0xFF818CF8)
                                    },
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${secondsLeft}s",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3 Immediate Grounding Actions
                Text(
                    text = "Ações Físicas de Choque Anti-Recaída:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ShockActionCard(
                        icon = Icons.Default.WaterDrop,
                        iconTint = BlueAccent,
                        title = "1. Água Gelada no Rosto Agora",
                        description = "Vá até a pia e jogue água fria nos olhos e nuca por 30 segundos. Ativa o reflexo de mergulho mamífero e desacelera o cérebro."
                    )

                    ShockActionCard(
                        icon = Icons.Default.ElectricBolt,
                        iconTint = CrimsonAlert,
                        title = "2. Abandone o Aparelho",
                        description = "Coloque o celular em outro cômodo ou longe de alcance. Não tente negociar com a tela."
                    )

                    ShockActionCard(
                        icon = Icons.Default.FitnessCenter,
                        iconTint = EmeraldPrimary,
                        title = "3. 20 Flexões ou 40 Polichinelos",
                        description = "Gaste o sangue da região pélvica canalizando a energia para os músculos superiores."
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stoic reminder
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "\"Não é porque as coisas são difíceis que nós não ousamos; é porque nós não ousamos que elas são difíceis.\"",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— Sêneca",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("dismiss_sos_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color(0xFF003816)
                    )
                ) {
                    Text(
                        text = "Venci a Onda! Voltar ao App",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ShockActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
