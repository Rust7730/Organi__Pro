package com.e243768.organipro_.core.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Extensiones de utilidad para diferentes tipos
 */

// String Extensions
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}

fun String.isValidEmail(): Boolean {
    return ValidationUtils.isValidEmail(this)
}

fun String.truncate(maxLength: Int, suffix: String = "..."): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength - suffix.length) + suffix
    } else {
        this
    }
}

// Int Extensions
fun Int.toFormattedPoints(): String {
    return "$this pts."
}

fun Int.toFormattedNumber(): String {
    return String.format(Locale.getDefault(), "%,d", this)
}

// Date Extensions
fun Date.isToday(): Boolean {
    return DateUtils.isToday(this)
}

fun Date.isTomorrow(): Boolean {
    return DateUtils.isTomorrow(this)
}

fun Date.isThisWeek(): Boolean {
    return DateUtils.isThisWeek(this)
}

fun Date.formatDisplay(): String {
    return DateUtils.formatDisplayDate(this)
}

fun Date.formatShort(): String {
    return DateUtils.formatShortDate(this)
}

fun Date.formatTime12h(): String {
    return DateUtils.formatTime12h(this)
}

fun Date.formatTime24h(): String {
    return DateUtils.formatTime24h(this)
}

// Long Extensions (for timestamps)
fun Long.toDate(): Date {
    return Date(this)
}

fun Long.formatAsDate(pattern: String = "dd/MM/yyyy HH:mm"): String {
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(Date(this))
}

// List Extensions
fun <T> List<T>.second(): T? {
    return if (this.size >= 2) this[1] else null
}

fun <T> List<T>.third(): T? {
    return if (this.size >= 3) this[2] else null
}

// Boolean Extensions
fun Boolean.toInt(): Int = if (this) 1 else 0

// Any Extensions
fun <T> T?.orDefault(default: T): T {
    return this ?: default
}