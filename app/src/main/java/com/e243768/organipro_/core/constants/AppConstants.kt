package com.e243768.organipro_.core.constants

object AppConstants {
    // App Info
    const val APP_NAME = "OrganiPro"
    const val APP_VERSION = "1.0.0"

    // Preferences
    const val PREFERENCES_NAME = "organipro_preferences"
    const val KEY_USER_ID = "user_id"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_FIRST_TIME = "first_time"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_LANGUAGE = "language"

    // Points System
    const val POINTS_LOW_PRIORITY = 50
    const val POINTS_MEDIUM_PRIORITY = 100
    const val POINTS_HIGH_PRIORITY = 200
    const val POINTS_STREAK_BONUS = 10
    const val XP_PER_LEVEL = 1000

    // Time Slots
    const val TIME_SLOT_DURATION_MINUTES = 60
    const val HOURS_PER_DAY = 24

    // Pagination
    const val PAGE_SIZE = 20
    const val INITIAL_PAGE = 0

    // Animation Durations (milliseconds)
    const val ANIMATION_DURATION_SHORT = 200L
    const val ANIMATION_DURATION_MEDIUM = 300L
    const val ANIMATION_DURATION_LONG = 500L

    // Network
    const val NETWORK_TIMEOUT = 30L // seconds
    const val MAX_RETRY_ATTEMPTS = 3

    // Date Formats
    const val DATE_FORMAT_DISPLAY = "d 'de' MMMM 'de' yyyy"
    const val DATE_FORMAT_SHORT = "dd/MM/yyyy"
    const val TIME_FORMAT_12H = "hh:mm a"
    const val TIME_FORMAT_24H = "HH:mm"
    const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    // Leaderboard
    const val TOP_USERS_COUNT = 10
    const val LEADERBOARD_REFRESH_INTERVAL = 300000L // 5 minutes in milliseconds

    // File Upload
    const val MAX_FILE_SIZE_MB = 10
    const val ALLOWED_FILE_EXTENSIONS = "pdf,jpg,jpeg,png,doc,docx"
}