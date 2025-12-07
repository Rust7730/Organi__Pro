package com.e243768.organipro_.core.constants

object FirebaseConstants {
    // Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_TASKS = "tasks"
    const val COLLECTION_LEADERBOARD = "leaderboard"
    const val COLLECTION_ACHIEVEMENTS = "achievements"
    const val COLLECTION_ATTACHMENTS = "attachments"
    const val COLLECTION_USER_STATS = "user_stats"


    // Sub-collections
    const val SUBCOLLECTION_DAILY_TASKS = "daily_tasks"
    const val SUBCOLLECTION_WEEKLY_TASKS = "weekly_tasks"
    const val SUBCOLLECTION_MONTHLY_TASKS = "monthly_tasks"
    const val SUBCOLLECTION_ATTACHMENTS = "attachments"

    // User Fields
    const val FIELD_USER_NAME = "name"
    const val FIELD_USER_ALIAS = "alias"
    const val FIELD_USER_EMAIL = "email"
    const val FIELD_USER_LEVEL = "level"
    const val FIELD_USER_XP = "xp"
    const val FIELD_USER_POINTS = "points"
    const val FIELD_USER_STREAK = "streak"
    const val FIELD_USER_AVATAR = "avatarUrl"
    const val FIELD_USER_CREATED_AT = "createdAt"
    const val FIELD_USER_UPDATED_AT = "updatedAt"

    // Task Fields
    const val FIELD_TASK_TITLE = "title"
    const val FIELD_TASK_DESCRIPTION = "description"
    const val FIELD_TASK_PRIORITY = "priority"
    const val FIELD_TASK_POINTS = "points"
    const val FIELD_TASK_DUE_DATE = "dueDate"
    const val FIELD_TASK_COMPLETED = "completed"
    const val FIELD_TASK_USER_ID = "userId"
    const val FIELD_TASK_CREATED_AT = "createdAt"

    // Storage Paths
    const val STORAGE_AVATARS = "avatars"
    const val STORAGE_ATTACHMENTS = "attachments"
    const val STORAGE_TEMP = "temp"

    // Error Messages
    const val ERROR_NETWORK = "Error de conexión. Verifica tu internet."
    const val ERROR_AUTH_FAILED = "Error de autenticación."
    const val ERROR_USER_NOT_FOUND = "Usuario no encontrado."
    const val ERROR_TASK_NOT_FOUND = "Tarea no encontrada."
    const val ERROR_PERMISSION_DENIED = "Permiso denegado."
    const val ERROR_UNKNOWN = "Error desconocido. Intenta de nuevo."
}