package com.e243768.organipro_.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.e243768.organipro_.core.constants.DatabaseConstants
import com.e243768.organipro_.domain.model.*
import java.util.Date

@Entity(
    tableName = DatabaseConstants.TABLE_TASKS,
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [DatabaseConstants.COLUMN_ID],
            childColumns = [DatabaseConstants.COLUMN_TASK_USER_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [DatabaseConstants.COLUMN_TASK_USER_ID]),
        Index(value = [DatabaseConstants.COLUMN_TASK_DUE_DATE]),
        Index(value = [DatabaseConstants.COLUMN_TASK_COMPLETED])
    ]
)
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = DatabaseConstants.COLUMN_ID)
    val id: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_TASK_USER_ID)
    val userId: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_TASK_TITLE)
    val title: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_TASK_DESCRIPTION)
    val description: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_TASK_PRIORITY)
    val priority: Priority,

    @ColumnInfo(name = "status")
    val status: TaskStatus,

    @ColumnInfo(name = DatabaseConstants.COLUMN_TASK_POINTS)
    val points: Int,

    @ColumnInfo(name = DatabaseConstants.COLUMN_TASK_DUE_DATE)
    val dueDate: Date?,

    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: String?,

    @ColumnInfo(name = "estimated_duration")
    val estimatedDuration: Int,

    @ColumnInfo(name = "progress")
    val progress: Float,

    @ColumnInfo(name = "tags")
    val tags: List<String>,

    @ColumnInfo(name = DatabaseConstants.COLUMN_CREATED_AT)
    val createdAt: Date,

    @ColumnInfo(name = DatabaseConstants.COLUMN_UPDATED_AT)
    val updatedAt: Date,

    @ColumnInfo(name = DatabaseConstants.COLUMN_TASK_COMPLETED)
    val completedAt: Date?,

    @ColumnInfo(name = "is_recurring")
    val isRecurring: Boolean,

    @ColumnInfo(name = "recurring_type")
    val recurringType: RecurringType?,

    @ColumnInfo(name = "recurring_interval")
    val recurringInterval: Int?,

    @ColumnInfo(name = "recurring_days_of_week")
    val recurringDaysOfWeek: List<Int>?,

    @ColumnInfo(name = "recurring_end_date")
    val recurringEndDate: Date?,

    @ColumnInfo(name = DatabaseConstants.COLUMN_SYNCED)
    val synced: Boolean = false
) {
    fun toDomain(): Task {
        val recurringPattern = if (isRecurring && recurringType != null) {
            RecurringPattern(
                type = recurringType,
                interval = recurringInterval ?: 1,
                daysOfWeek = recurringDaysOfWeek ?: emptyList(),
                endDate = recurringEndDate
            )
        } else null

        return Task(
            id = id,
            userId = userId,
            title = title,
            description = description,
            priority = priority,
            status = status,
            points = points,
            dueDate = dueDate,
            scheduledTime = scheduledTime,
            estimatedDuration = estimatedDuration,
            progress = progress,
            attachments = emptyList(), // Se cargan por separado
            tags = tags,
            createdAt = createdAt,
            updatedAt = updatedAt,
            completedAt = completedAt,
            isRecurring = isRecurring,
            recurringPattern = recurringPattern
        )
    }

    companion object {
        fun fromDomain(task: Task, synced: Boolean = false): TaskEntity {
            return TaskEntity(
                id = task.id,
                userId = task.userId,
                title = task.title,
                description = task.description,
                priority = task.priority,
                status = task.status,
                points = task.points,
                dueDate = task.dueDate,
                scheduledTime = task.scheduledTime,
                estimatedDuration = task.estimatedDuration,
                progress = task.progress,
                tags = task.tags,
                createdAt = task.createdAt,
                updatedAt = task.updatedAt,
                completedAt = task.completedAt,
                isRecurring = task.isRecurring,
                recurringType = task.recurringPattern?.type,
                recurringInterval = task.recurringPattern?.interval,
                recurringDaysOfWeek = task.recurringPattern?.daysOfWeek,
                recurringEndDate = task.recurringPattern?.endDate,
                synced = synced
            )
        }
    }
}