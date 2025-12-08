package com.e243768.organipro_.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.e243768.organipro_.core.constants.DatabaseConstants
import com.e243768.organipro_.domain.model.UserStats

@Entity(
    tableName = "user_stats",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [DatabaseConstants.COLUMN_ID],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserStatsEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "total_points")
    val totalPoints: Int,

    @ColumnInfo(name = "weekly_points")
    val weeklyPoints: Int,

    @ColumnInfo(name = "monthly_points")
    val monthlyPoints: Int,

    @ColumnInfo(name = "tasks_completed")
    val tasksCompleted: Int,

    @ColumnInfo(name = "tasks_completed_today")
    val tasksCompletedToday: Int,

    @ColumnInfo(name = "tasks_completed_this_week")
    val tasksCompletedThisWeek: Int,

    @ColumnInfo(name = "tasks_completed_this_month")
    val tasksCompletedThisMonth: Int,

    @ColumnInfo(name = "current_streak")
    val currentStreak: Int,

    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int,

    @ColumnInfo(name = "average_tasks_per_day")
    val averageTasksPerDay: Float,

    @ColumnInfo(name = "completion_rate")
    val completionRate: Float,

    @ColumnInfo(name = "total_tasks_created")
    val totalTasksCreated: Int,

    @ColumnInfo(name = "high_priority_completed")
    val highPriorityCompleted: Int,

    @ColumnInfo(name = "medium_priority_completed")
    val mediumPriorityCompleted: Int,

    @ColumnInfo(name = "low_priority_completed")
    val lowPriorityCompleted: Int
) {
    fun toDomain(): UserStats {
        return UserStats(
            userId = userId,
            totalPoints = totalPoints,
            weeklyPoints = weeklyPoints,
            monthlyPoints = monthlyPoints,
            tasksCompleted = tasksCompleted,
            tasksCompletedToday = tasksCompletedToday,
            tasksCompletedThisWeek = tasksCompletedThisWeek,
            tasksCompletedThisMonth = tasksCompletedThisMonth,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            averageTasksPerDay = averageTasksPerDay,
            completionRate = completionRate,
            totalTasksCreated = totalTasksCreated,
            highPriorityCompleted = highPriorityCompleted,
            mediumPriorityCompleted = mediumPriorityCompleted,
            lowPriorityCompleted = lowPriorityCompleted
        )
    }

    companion object {
        fun fromDomain(stats: UserStats): UserStatsEntity {
            return UserStatsEntity(
                userId = stats.userId,
                totalPoints = stats.totalPoints,
                weeklyPoints = stats.weeklyPoints,
                monthlyPoints = stats.monthlyPoints,
                tasksCompleted = stats.tasksCompleted,
                tasksCompletedToday = stats.tasksCompletedToday,
                tasksCompletedThisWeek = stats.tasksCompletedThisWeek,
                tasksCompletedThisMonth = stats.tasksCompletedThisMonth,
                currentStreak = stats.currentStreak,
                longestStreak = stats.longestStreak,
                averageTasksPerDay = stats.averageTasksPerDay,
                completionRate = stats.completionRate,
                totalTasksCreated = stats.totalTasksCreated,
                highPriorityCompleted = stats.highPriorityCompleted,
                mediumPriorityCompleted = stats.mediumPriorityCompleted,
                lowPriorityCompleted = stats.lowPriorityCompleted
            )
        }
    }
}