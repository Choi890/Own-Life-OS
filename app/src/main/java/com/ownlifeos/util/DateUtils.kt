package com.ownlifeos.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {
    private val displayFormatter = DateTimeFormatter.ofPattern("M월 d일 EEEE", Locale.KOREAN)

    fun todayKey(): String = LocalDate.now().toString()

    fun daysAgo(days: Long): String = LocalDate.now().minusDays(days).toString()

    fun daysBefore(date: String, days: Long): String =
        LocalDate.parse(date).minusDays(days).toString()

    fun displayDate(date: String): String = runCatching {
        LocalDate.parse(date).format(displayFormatter)
    }.getOrDefault(date)
}
