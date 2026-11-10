package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DayRecord
import com.example.model.DayStatus
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailBottomSheet(
    date: String,
    record: DayRecord?,
    onDismiss: () -> Unit,
    onSave: (status: DayStatus, note: String, urgeLevel: Int, trigger: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentStatus by remember(record) {
        mutableStateOf(record?.status ?: if (DateUtils.isFuture(date)) DayStatus.FUTURE else DayStatus.UNMARKED)
    }
    var noteText by remember(record) { mutableStateOf(record?.note ?: "") }
    var urgeSlider by remember(record) { mutableFloatStateOf(record?.urgeLevel?.toFloat() ?: 0f) }
    var triggerText by remember(record) { mutableStateOf(record?.triggerReason ?: "") }

    val formattedDate = DateUtils.formatDisplayDate(date)
    val isFuture = DateUtils.isFuture(date)
    val isPast = DateUtils.isPast(date)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        modifier = Modifier.testTag("day_detail_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = when {
                                DateUtils.isToday(date) -> "Hoje (Em andamento)"
                                isFuture -> "Data Futura (Bloqueada)"
                                else -> "Registro de Histórico"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isFuture) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceCard)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Este dia ainda não chegou. Conquiste um dia de cada vez!",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                // Status Selection Buttons
                Text(
                    text = "Status do Dia",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Limpo (Verde)
                    val isClean = currentStatus == DayStatus.CLEAN
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isClean) EmeraldPrimary.copy(alpha = 0.2f) else DarkSurfaceCard
                            )
                            .clickable { currentStatus = DayStatus.CLEAN }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isClean) EmeraldPrimary else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Limpo (Verde)",
                                color = if (isClean) EmeraldPrimary else TextSecondary,
                                fontWeight = if (isClean) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Recaída (Vermelho)
                    val isRelapse = currentStatus == DayStatus.RELAPSE
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isRelapse) CrimsonAlert.copy(alpha = 0.2f) else DarkSurfaceCard
                            )
                            .clickable { currentStatus = DayStatus.RELAPSE }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isRelapse) CrimsonAlert else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Recaída (Caiu)",
                                color = if (isRelapse) CrimsonAlert else TextSecondary,
                                fontWeight = if (isRelapse) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Não Marcado (Cinza)
                    val isUnmarked = currentStatus == DayStatus.UNMARKED
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isUnmarked) Color.White.copy(alpha = 0.15f) else DarkSurfaceCard
                            )
                            .clickable { currentStatus = DayStatus.UNMARKED }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(DarkOutline)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isPast) "Não Marcado" else "Pendente",
                                color = if (isUnmarked) TextPrimary else TextSecondary,
                                fontWeight = if (isUnmarked) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                if (isPast && currentStatus == DayStatus.UNMARKED) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aviso: Dias do passado não marcados são contabilizados como queda/quebra de sequência.",
                        color = CrimsonAlert,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Urge Level Slider
                Text(
                    text = "Nível de Vontade / Urgência Sentida: ${
                        when (urgeSlider.toInt()) {
                            0 -> "0 (Nenhuma vontade)"
                            1 -> "1 (Leve distração)"
                            2 -> "2 (Média - exigiu esforço)"
                            else -> "3 (Severa - crise superada)"
                        }
                    }",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                )
                Slider(
                    value = urgeSlider,
                    onValueChange = { urgeSlider = it },
                    valueRange = 0f..3f,
                    steps = 2,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldPrimary,
                        activeTrackColor = EmeraldPrimary,
                        inactiveTrackColor = DarkOutline
                    )
                )

                if (currentStatus == DayStatus.RELAPSE) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Gatilho identificado:",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CrimsonAlert
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = triggerText,
                        onValueChange = { triggerText = it },
                        placeholder = { Text("Ex: Celular na cama, cansaço, estresse...", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CrimsonAlert,
                            unfocusedBorderColor = DarkOutline,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes
                Text(
                    text = "Reflexão do Dia / Hábitos Praticados",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = {
                        Text(
                            "Ex: Treino concluído, leitura feita, energia sob controle.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = DarkOutline,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    maxLines = 4
                )

                if (record?.penaltyAssigned?.isNotBlank() == true) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceCard)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Punição associada: ${record.penaltyAssigned}",
                            color = CrimsonAlert,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save button
                Button(
                    onClick = {
                        onSave(currentStatus, noteText, urgeSlider.toInt(), triggerText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_day_record_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentStatus == DayStatus.RELAPSE) CrimsonAlert else EmeraldPrimary,
                        contentColor = if (currentStatus == DayStatus.RELAPSE) Color.White else Color(0xFF003816)
                    )
                ) {
                    Text(
                        text = "Salvar Alterações",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
