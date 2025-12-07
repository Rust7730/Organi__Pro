package com.e243768.organipro_.core.util

import com.e243768.organipro_.core.constants.AppConstants
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val spanishLocale = Locale("es", "ES")

    /**
     * Formatea una fecha con el formato de visualización
     */
    fun formatDisplayDate(date: Date): String {
        val format = SimpleDateFormat(AppConstants.DATE_FORMAT_DISPLAY, spanishLocale)
        return format.format(date)
    }

    /**
     * Formatea una fecha con formato corto
     */
    fun formatShortDate(date: Date): String {
        val format = SimpleDateFormat(AppConstants.DATE_FORMAT_SHORT, spanishLocale)
        return format.format(date)
    }

    /**
     * Formatea una hora en formato 12h
     */
    fun formatTime12h(date: Date): String {
        val format = SimpleDateFormat(AppConstants.TIME_FORMAT_12H, spanishLocale)
        return format.format(date)
    }

    /**
     * Formatea una hora en formato 24h
     */
    fun formatTime24h(date: Date): String {
        val format = SimpleDateFormat(AppConstants.TIME_FORMAT_24H, spanishLocale)
        return format.format(date)
    }

    /**
     * Obtiene el inicio del día actual
     */
    fun getStartOfDay(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * Obtiene el fin del día actual
     */
    fun getEndOfDay(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    /**
     * Obtiene el inicio de la semana (lunes)
     */
    fun getStartOfWeek(date: Date = Date()): Date {
        val calendar = Calendar.getInstance(spanishLocale)
        calendar.time = date
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return getStartOfDay(calendar.time)
    }

    /**
     * Obtiene el fin de la semana (domingo)
     */
    fun getEndOfWeek(date: Date = Date()): Date {
        val calendar = Calendar.getInstance(spanishLocale)
        calendar.time = date
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return getEndOfDay(calendar.time)
    }

    /**
     * Obtiene el inicio del mes
     */
    fun getStartOfMonth(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return getStartOfDay(calendar.time)
    }

    /**
     * Obtiene el fin del mes
     */
    fun getEndOfMonth(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return getEndOfDay(calendar.time)
    }

    /**
     * Verifica si una fecha es hoy
     */
    fun isToday(date: Date): Boolean {
        val today = getStartOfDay()
        val target = getStartOfDay(date)
        return today == target
    }

    /**
     * Verifica si una fecha es mañana
     */
    fun isTomorrow(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val tomorrow = getStartOfDay(calendar.time)
        val target = getStartOfDay(date)
        return tomorrow == target
    }

    /**
     * Verifica si una fecha es de esta semana
     */
    fun isThisWeek(date: Date): Boolean {
        val startOfWeek = getStartOfWeek()
        val endOfWeek = getEndOfWeek()
        return date.after(startOfWeek) && date.before(endOfWeek) ||
                date == startOfWeek || date == endOfWeek
    }

    /**
     * Obtiene el número de días entre dos fechas
     */
    fun getDaysBetween(startDate: Date, endDate: Date): Int {
        val diff = endDate.time - startDate.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    /**
     * Convierte timestamp a Date
     */
    fun timestampToDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    /**
     * Convierte Date a timestamp
     */
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}