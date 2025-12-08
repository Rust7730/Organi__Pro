package com.e243768.organipro_.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.e243768.organipro_.core.constants.DatabaseConstants
import com.e243768.organipro_.domain.model.UserRank

@Entity(tableName = DatabaseConstants.TABLE_LEADERBOARD)
data class UserRankEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "user_name")
    val userName: String,

    @ColumnInfo(name = "user_alias")
    val userAlias: String,

    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,

    @ColumnInfo(name = "rank")
    val rank: Int,

    @ColumnInfo(name = "points")
    val points: Int,

    @ColumnInfo(name = "weekly_points")
    val weeklyPoints: Int,

    @ColumnInfo(name = "monthly_points")
    val monthlyPoints: Int,

    @ColumnInfo(name = "level")
    val level: Int,

    @ColumnInfo(name = "streak")
    val streak: Int,

    @ColumnInfo(name = "is_current_user")
    val isCurrentUser: Boolean,

    @ColumnInfo(name = DatabaseConstants.COLUMN_UPDATED_AT)
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): UserRank {
        return UserRank(
            userId = userId,
            userName = userName,
            userAlias = userAlias,
            avatarUrl = avatarUrl,
            rank = rank,
            points = points,
            weeklyPoints = weeklyPoints,
            monthlyPoints = monthlyPoints,
            level = level,
            streak = streak,
            isCurrentUser = isCurrentUser
        )
    }

    companion object {
        fun fromDomain(userRank: UserRank): UserRankEntity {
            return UserRankEntity(
                userId = userRank.userId,
                userName = userRank.userName,
                userAlias = userRank.userAlias,
                avatarUrl = userRank.avatarUrl,
                rank = userRank.rank,
                points = userRank.points,
                weeklyPoints = userRank.weeklyPoints,
                monthlyPoints = userRank.monthlyPoints,
                level = userRank.level,
                streak = userRank.streak,
                isCurrentUser = userRank.isCurrentUser
            )
        }
    }
}