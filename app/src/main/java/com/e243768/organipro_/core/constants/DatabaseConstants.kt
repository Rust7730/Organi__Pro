package com.e243768.organipro_.core.constants

object DatabaseConstants {
    // Database
    const val DATABASE_NAME = "organipro_database"
    const val DATABASE_VERSION = 1

    // Tables
    const val TABLE_USER = "user"
    const val TABLE_TASKS = "tasks"
    const val TABLE_DAILY_TASKS = "daily_tasks"
    const val TABLE_WEEKLY_TASKS = "weekly_tasks"
    const val TABLE_MONTHLY_TASKS = "monthly_tasks"
    const val TABLE_ATTACHMENTS = "attachments"
    const val TABLE_LEADERBOARD = "leaderboard"

    // Common Columns
    const val COLUMN_ID = "id"
    const val COLUMN_CREATED_AT = "created_at"
    const val COLUMN_UPDATED_AT = "updated_at"
    const val COLUMN_SYNCED = "synced"

    // User Columns
    const val COLUMN_USER_NAME = "name"
    const val COLUMN_USER_ALIAS = "alias"
    const val COLUMN_USER_EMAIL = "email"
    const val COLUMN_USER_LEVEL = "level"
    const val COLUMN_USER_XP = "xp"
    const val COLUMN_USER_POINTS = "total_points"
    const val COLUMN_USER_STREAK = "streak"
    const val COLUMN_USER_AVATAR_URL = "avatar_url"

    // Task Columns
    const val COLUMN_TASK_TITLE = "title"
    const val COLUMN_TASK_DESCRIPTION = "description"
    const val COLUMN_TASK_PRIORITY = "priority"
    const val COLUMN_TASK_POINTS = "points"
    const val COLUMN_TASK_DUE_DATE = "due_date"
    const val COLUMN_TASK_COMPLETED = "completed"
    const val COLUMN_TASK_USER_ID = "user_id"

    // Attachment Columns
    const val COLUMN_ATTACHMENT_NAME = "name"
    const val COLUMN_ATTACHMENT_TYPE = "type"
    const val COLUMN_ATTACHMENT_SIZE = "size"
    const val COLUMN_ATTACHMENT_URL = "url"
    const val COLUMN_ATTACHMENT_TASK_ID = "task_id"
}