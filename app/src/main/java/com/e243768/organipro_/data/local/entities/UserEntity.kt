package com.e243768.organipro_.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.e243768.organipro_.core.constants.DatabaseConstants
import com.e243768.organipro_.domain.model.User
import java.util.Date

@Entity(tableName = DatabaseConstants.TABLE_USER)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = DatabaseConstants.COLUMN_ID)
    val id: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_USER_NAME)
    val name: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_USER_ALIAS)
    val alias: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_USER_EMAIL)
    val email: String,

    @ColumnInfo(name = DatabaseConstants.COLUMN_USER_AVATAR_URL)
    val avatarUrl: String?,

    @ColumnInfo(name = DatabaseConstants.COLUMN_USER_LEVEL)
    val level: Int,

    @ColumnInfo(name = DatabaseConstants.COLUMN_USER_XP)
    val currentXP: Int,

    @ColumnInfo(name = DatabaseConstants.COLUMN_USER_POINTS)
    val totalPoints: Int,

    @ColumnInfo(name = DatabaseConstants.COLUMN_USER_STREAK)
    val currentStreak: Int,

    @ColumnInfo(name = "longest_streak")
    val longestStreak: Int,

    @ColumnInfo(name = "tasks_completed")
    val tasksCompleted: Int,

    @ColumnInfo(name = DatabaseConstants.COLUMN_CREATED_AT)
    val createdAt: Date,

    @ColumnInfo(name = DatabaseConstants.COLUMN_UPDATED_AT)
    val updatedAt: Date,

    @ColumnInfo(name = "last_login_at")
    val lastLoginAt: Date?,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean,

    @ColumnInfo(name = DatabaseConstants.COLUMN_SYNCED)
    val synced: Boolean = false
) {
    fun toDomain(): User {
        return User(
            id = id,
            name = name,
            alias = alias,
            email = email,
            avatarUrl = avatarUrl,
            level = level,
            currentXP = currentXP,
            totalPoints = totalPoints,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            tasksCompleted = tasksCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt,
            lastLoginAt = lastLoginAt,
            isActive = isActive
        )
    }

    companion object {
        fun fromDomain(user: User, synced: Boolean = false): UserEntity {
            return UserEntity(
                id = user.id,
                name = user.name,
                alias = user.alias,
                email = user.email,
                avatarUrl = user.avatarUrl,
                level = user.level,
                currentXP = user.currentXP,
                totalPoints = user.totalPoints,
                currentStreak = user.currentStreak,
                longestStreak = user.longestStreak,
                tasksCompleted = user.tasksCompleted,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                lastLoginAt = user.lastLoginAt,
                isActive = user.isActive,
                synced = synced
            )
        }
    }
}