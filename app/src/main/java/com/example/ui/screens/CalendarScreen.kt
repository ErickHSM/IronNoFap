package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.ui.components.GlassCard
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DayRecord
import com.example.model.DayStatus
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.util.DateUtils
import java.util.Calendar
import java.util.Locale

data class CalendarDayItem(
    val dayNumber: Int,
    val dateStr: String,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isPast: Boolean,
    val isFuture: Boolean,
    val record: DayRecord?
)

@Composable
fun CalendarScreen(
    records: Map<String, DayRecord>,
    selectedMonth: Int,
    selectedYear: Int,
    onChangeMonth: (delta: Int) -> Unit,
    onResetCurrentMonth: () -> Unit,
    onSelectDate: (date: String) -> Unit
) {
    val monthName = DateUtils.getMonthNamePt(selectedMonth)
    val todayStr = DateUtils.getTodayString()

    // Generate days for grid
    val calendarDays = remember(selectedMonth, selectedYear, records) {
        buildCalendarDays(selectedMonth, selectedYear, records, todayStr)
    }

    // Month statistics
    val cleanInMonth = calendarDays.count { it.isCurrentMonth && it.record?.status == DayStatus.CLEAN }
    val relapseInMonth = calendarDays.count { it.isCurrentMonth && it.record?.status == DayStatus.RELAPSE }
    val unmarkedPastInMonth = calendarDays.count {
        it.isCurrentMonth && it.isPast && (it.record == null || it.record.status == DayStatus.UNMARKED)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("calendar_screen")
    ) {
        // Month Header & Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$monthName $selectedYear",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Calendário de Fortitude",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = onResetCurrentMonth,
                    modifier = Modifier.testTag("current_month_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = "Hoje",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hoje", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { onChangeMonth(-1) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Mês Anterior",
                        tint = TextPrimary
                    )
                }

                IconButton(
                    onClick = { onChangeMonth(1) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Próximo Mês",
                        tint = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Month Summary Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MonthMetricPill(
                label = "Vencidos",
                count = cleanInMonth,
                color = EmeraldPrimary,
                modifier = Modifier.weight(1f)
            )
            MonthMetricPill(
                label = "Recaídas",
                count = relapseInMonth,
                color = CrimsonAlert,
                modifier = Modifier.weight(1f)
            )
            MonthMetricPill(
                label = "Não Marcados",
                count = unmarkedPastInMonth,
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar Card with Frosted Glassmorphism & Neon Glow
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            glowColor = Color(0x3300E676)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Day of Week Header: DOM, SEG, TER, QUA, QUI, SEX, SÁB
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val daysOfWeek = listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB")
                    daysOfWeek.forEach { dayName ->
                        Text(
                            text = dayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                fontSize = 11.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 7x5 or 7x6 Grid of Days
                val rows = calendarDays.chunked(7)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    rows.forEach { rowDays ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowDays.forEach { day ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                ) {
                                    DayCell(day = day, onClick = { onSelectDate(day.dateStr) })
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rule & Legend Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Regra de Ouro do IronWill:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "• Verde: Dia que você venceu e ficou sem pornografia.\n• Vermelho: Recaída confessada e punição assumida.\n• Cinza com alerta: Dia passado que você não marcou. Por regra, a omissão é considerada queda.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Dica: Toque em qualquer dia para ver ou editar detalhes.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun DayCell(
    day: CalendarDayItem,
    onClick: () -> Unit
) {
    if (!day.isCurrentMonth) {
        // Outside current month: very faint
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${day.dayNumber}",
                color = TextTertiary.copy(alpha = 0.3f),
                fontSize = 11.sp
            )
        }
        return
    }

    val status = day.record?.status
    val isClean = status == DayStatus.CLEAN
    val isRelapse = status == DayStatus.RELAPSE
    val isUnmarkedPast = day.isPast && (status == null || status == DayStatus.UNMARKED)
    val isUnmarkedToday = day.isToday && (status == null || status == DayStatus.UNMARKED)

    // Cell Styling:
    // CLEAN -> Vibrant Emerald Green background!
    // RELAPSE -> Crimson Red background!
    // UNMARKED PAST -> Dark slate with subtle red cross/indicator
    // TODAY UNMARKED -> Border highlight
    // FUTURE -> Locked subtle dark
    val backgroundColor = when {
        isClean -> EmeraldPrimary
        isRelapse -> CrimsonAlert
        isUnmarkedPast -> Color(0xFF2B1D20) // Subtle dark crimson-gray for past missed day
        isUnmarkedToday -> DarkSurfaceCard
        else -> DarkSurfaceCard
    }

    val contentColor = when {
        isClean -> Color(0xFF003816)
        isRelapse -> Color.White
        isUnmarkedPast -> Color(0xFFFCA5A5)
        day.isToday -> TextPrimary
        day.isFuture -> TextTertiary
        else -> TextSecondary
    }

    val borderModifier = when {
        day.isToday -> Modifier.border(2.dp, if (isClean) EmeraldPrimary else if (isRelapse) CrimsonAlert else Color.White, RoundedCornerShape(10.dp))
        isUnmarkedPast -> Modifier.border(0.8.dp, CrimsonAlert.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
        else -> Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .then(borderModifier)
            .clickable { onClick() }
            .testTag("day_cell_${day.dateStr}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${day.dayNumber}",
                color = contentColor,
                fontWeight = if (isClean || isRelapse || day.isToday) FontWeight.Black else FontWeight.Medium,
                fontSize = 12.sp
            )

            // Small indicator underneath
            when {
                isClean -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Limpo",
                        tint = Color(0xFF003816),
                        modifier = Modifier.size(11.dp)
                    )
                }
                isRelapse -> {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Recaída",
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
                isUnmarkedPast -> {
                    // Marker for missed day that user did not mark
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(CrimsonAlert)
                    )
                }
                isUnmarkedToday -> {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthMetricPill(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        borderColor = color.copy(alpha = 0.5f),
        glowColor = color.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontSize = 10.sp
                ),
                maxLines = 1
            )
        }
    }
}

private fun buildCalendarDays(
    month: Int,
    year: Int,
    records: Map<String, DayRecord>,
    todayStr: String
): List<CalendarDayItem> {
    val items = mutableListOf<CalendarDayItem>()

    val cal = Calendar.getInstance()
    cal.set(Calendar.YEAR, year)
    cal.set(Calendar.MONTH, month)
    cal.set(Calendar.DAY_OF_MONTH, 1)

    // First day of week for 1st of month (1: Sunday, 2: Monday, etc.)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Days from previous month to fill the first row
    val prevMonthCal = cal.clone() as Calendar
    prevMonthCal.add(Calendar.MONTH, -1)
    val daysInPrevMonth = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leadingDays = firstDayOfWeek - Calendar.SUNDAY

    for (i in leadingDays - 1 downTo 0) {
        val d = daysInPrevMonth - i
        val prevMonth = prevMonthCal.get(Calendar.MONTH)
        val prevYear = prevMonthCal.get(Calendar.YEAR)
        val dateStr = String.format(Locale.US, "%04d-%02d-%02d", prevYear, prevMonth + 1, d)
        items.add(
            CalendarDayItem(
                dayNumber = d,
                dateStr = dateStr,
                isCurrentMonth = false,
                isToday = dateStr == todayStr,
                isPast = dateStr < todayStr,
                isFuture = dateStr > todayStr,
                record = records[dateStr]
            )
        )
    }

    // Days in current month
    for (d in 1..daysInMonth) {
        val dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, d)
        items.add(
            CalendarDayItem(
                dayNumber = d,
                dateStr = dateStr,
                isCurrentMonth = true,
                isToday = dateStr == todayStr,
                isPast = dateStr < todayStr,
                isFuture = dateStr > todayStr,
                record = records[dateStr]
            )
        )
    }

    // Trailing days from next month to complete the last row (up to multiple of 7)
    val remaining = (7 - (items.size % 7)) % 7
    val nextMonthCal = cal.clone() as Calendar
    nextMonthCal.add(Calendar.MONTH, 1)
    val nextMonth = nextMonthCal.get(Calendar.MONTH)
    val nextYear = nextMonthCal.get(Calendar.YEAR)

    for (d in 1..remaining) {
        val dateStr = String.format(Locale.US, "%04d-%02d-%02d", nextYear, nextMonth + 1, d)
        items.add(
            CalendarDayItem(
                dayNumber = d,
                dateStr = dateStr,
                isCurrentMonth = false,
                isToday = dateStr == todayStr,
                isPast = dateStr < todayStr,
                isFuture = dateStr > todayStr,
                record = records[dateStr]
            )
        )
    }

    return items
}
