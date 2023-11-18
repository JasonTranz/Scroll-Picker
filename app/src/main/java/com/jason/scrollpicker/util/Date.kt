package com.jason.scrollpicker.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {
    private const val DATE_FORMAT = "dd"
    private const val LONG_MONTH_FORMAT = "MMMM"
    private const val SHORT_MONTH_FORMAT = "MM"
    private const val YEAR_FORMAT = "yyyy"

    fun getMonths(year: Int, format: String = LONG_MONTH_FORMAT): List<String> {
        val months = mutableListOf<String>()
        val monthFormatter = SimpleDateFormat(SHORT_MONTH_FORMAT, Locale.getDefault())
        val yearFormatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())
        val monthCalendar = Calendar.getInstance()

        val maxMonth = if (year == yearFormatter.format(monthCalendar.timeInMillis).toIntOrNull()) {
            monthFormatter.format(monthCalendar.timeInMillis).toIntOrNull() ?: 0
        } else {
            12
        }

        for (i in 0..<maxMonth) {
            val calendar = Calendar.getInstance()
            calendar.set(year, i, 1)
            months.add(SimpleDateFormat(format, Locale.getDefault()).format(calendar.timeInMillis))
        }

        return months
    }

    fun getYears(maxYear: Int): List<String> {
        val years = mutableListOf<String>()
        val yearFormatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())
        val calendar = Calendar.getInstance()

        val yearLimited = try {
            if (maxYear == yearFormatter.format(calendar.timeInMillis).toIntOrNull()) {
                getCurrentYear()
            } else {
                maxYear
            }
        } catch (ex: Exception) {
            maxYear
        }

        for (i in 1970..yearLimited) {
            years.add(i.toString())
        }

        return years
    }

    fun getDayOfMonth(year: Int, month: Int): List<String> {
        val days = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 0)

        val monthYear = "$month-$year"
        val currentMonthYear = "${getCurrentMonth()}-${getCurrentYear()}"

        val maxDate = if (monthYear == currentMonthYear) {
            getCurrentDate()
        } else {
            calendar.getActualMaximum(Calendar.DATE)
        }

        for (i in 1..maxDate) {
            days.add(i.toString())
        }

        return days
    }

    fun getCurrentYear(): Int {
        val formatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())
        val calendar = Calendar.getInstance()
        return formatter.format(calendar.timeInMillis).toIntOrNull() ?: 0
    }

    private fun getCurrentMonth(): Int {
        val formatter = SimpleDateFormat(SHORT_MONTH_FORMAT, Locale.getDefault())
        val calendar = Calendar.getInstance()
        return formatter.format(calendar.timeInMillis).toIntOrNull() ?: 0
    }

    private fun getCurrentDate(): Int {
        val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val calendar = Calendar.getInstance()
        return formatter.format(calendar.timeInMillis).toIntOrNull() ?: 0
    }

    fun formatMonth(month: String, defaultFormat: String = LONG_MONTH_FORMAT): Int {
        val defaultFormatter = SimpleDateFormat(defaultFormat, Locale.getDefault())
        val formatter = SimpleDateFormat(SHORT_MONTH_FORMAT, Locale.getDefault())

        val date: Date? = try {
            defaultFormatter.parse(month)
        } catch (ex: Exception) {
            null
        }

        return date?.let { formatter.format(it).toIntOrNull() } ?: 0
    }
}