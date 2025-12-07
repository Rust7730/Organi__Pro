package com.e243768.organipro_.data.local.database

import androidx.room.TypeConverter
import com.e243768.organipro_.domain.model.*
import java.util.Date

class Converters {

    // Date Converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // List<String> Converters
    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        return value?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return list?.joinToString(",") ?: ""
    }

    // Priority Converters
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }

    // TaskStatus Converters
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus {
        return TaskStatus.valueOf(value)
    }

    // AttachmentType Converters
    @TypeConverter
    fun fromAttachmentType(type: AttachmentType): String {
        return type.name
    }

    @TypeConverter
    fun toAttachmentType(value: String): AttachmentType {
        return AttachmentType.valueOf(value)
    }

    // AchievementType Converters
    @TypeConverter
    fun fromAchievementType(type: AchievementType): String {
        return type.name
    }

    @TypeConverter
    fun toAchievementType(value: String): AchievementType {
        return AchievementType.valueOf(value)
    }

    // RecurringType Converters
    @TypeConverter
    fun fromRecurringType(type: RecurringType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toRecurringType(value: String?): RecurringType? {
        return value?.let { RecurringType.valueOf(it) }
    }

    // List<Int> Converters (para días de la semana)
    @TypeConverter
    fun fromIntList(value: String?): List<Int> {
        return value?.split(",")
            ?.filter { it.isNotEmpty() }
            ?.mapNotNull { it.toIntOrNull() }
            ?: emptyList()
    }

    @TypeConverter
    fun toIntList(list: List<Int>?): String {
        return list?.joinToString(",") ?: ""
    }
}