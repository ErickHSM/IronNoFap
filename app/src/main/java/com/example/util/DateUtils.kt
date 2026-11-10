package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val PATTERN = "yyyy-MM-dd"

    fun getTodayString(): String {
        val sdf = SimpleDateFormat(PATTERN, Locale.US)
        return sdf.format(Date())
    }

    fun parseDate(dateStr: String): Date? {
        return try {
            val sdf = SimpleDateFormat(PATTERN, Locale.US)
            sdf.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat(PATTERN, Locale.US)
        return sdf.format(date)
    }

    fun getDaysDifference(from: String, to: String): Int {
        val d1 = parseDate(from) ?: return 0
        val d2 = parseDate(to) ?: return 0
        val diff = d2.time - d1.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun addDays(dateStr: String, days: Int): String {
        val date = parseDate(dateStr) ?: return dateStr
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, days)
        return formatDate(cal.time)
    }

    fun getPreviousDay(dateStr: String): String {
        return addDays(dateStr, -1)
    }

    fun getNextDay(dateStr: String): String {
        return addDays(dateStr, 1)
    }

    fun isToday(dateStr: String): Boolean {
        return dateStr == getTodayString()
    }

    fun isFuture(dateStr: String): Boolean {
        val today = getTodayString()
        return dateStr > today
    }

    fun isPast(dateStr: String): Boolean {
        val today = getTodayString()
        return dateStr < today
    }

    fun getMonthNamePt(monthIndex: Int): String {
        val months = listOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )
        return months.getOrElse(monthIndex) { "" }
    }

    fun formatDisplayDate(dateStr: String): String {
        val date = parseDate(dateStr) ?: return dateStr
        val sdf = SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("pt", "BR"))
        return sdf.format(date)
    }

    fun formatShortDisplayDate(dateStr: String): String {
        val date = parseDate(dateStr) ?: return dateStr
        val sdf = SimpleDateFormat("dd/MM", Locale("pt", "BR"))
        return sdf.format(date)
    }
}
