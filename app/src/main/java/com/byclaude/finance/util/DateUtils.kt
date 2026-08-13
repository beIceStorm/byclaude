package com.byclaude.finance.util

import java.time.LocalDate
import java.time.YearMonth

object DateUtils {

    private val monthsGenitive = listOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )

    private val monthsNominative = listOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )

    fun formatDayHeader(date: LocalDate): String {
        val today = LocalDate.now()
        return when (date) {
            today -> "Сегодня"
            today.minusDays(1) -> "Вчера"
            else -> {
                val base = "${date.dayOfMonth} ${monthsGenitive[date.monthValue - 1]}"
                if (date.year != today.year) "$base ${date.year}" else base
            }
        }
    }

    fun formatMonthYear(yearMonth: YearMonth): String {
        return "${monthsNominative[yearMonth.monthValue - 1]} ${yearMonth.year}"
    }
}
