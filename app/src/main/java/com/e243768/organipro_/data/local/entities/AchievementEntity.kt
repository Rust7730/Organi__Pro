package com.e243768.organipro_.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.e243768.organipro_.core.constants.DatabaseConstants
import com.e243768.organipro_.domain.model.Achievement
import com.e243768.organipro_.domain.model.AchievementType
import java.util.Date

@Entity(
    tableName = "achievements",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = [DatabaseConstants.COLUMN_ID],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class AchievementEntity(
    @PrimaryKey
    @ColumnInfo(name = DatabaseConstants.COLUMN_ID)
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "type")
    val type: AchievementType,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "icon_url")
    val iconUrl: String?,

    @ColumnInfo(name = "progress")
    val progress: Int,

    @ColumnInfo(name = "target")
    val target: Int,

    @ColumnInfo(name = "is_unlocked")
    val isUnlocked: Boolean,

    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Date?,

    @ColumnInfo(name = "reward_points")
    val rewardPoints: Int
) {
    fun toDomain(): Achievement {
        return Achievement(
            id = id,
            userId = userId,
            type = type,
            name = name,
            description = description,
            iconUrl = iconUrl,
            progress = progress,
            target = target,
            isUnlocked = isUnlocked,
            unlockedAt = unlockedAt,
            rewardPoints = rewardPoints
        )
    }

    companion object {
        fun fromDomain(achievement: Achievement): AchievementEntity {
            return AchievementEntity(
                id = achievement.id,
                userId = achievement.userId,
                type = achievement.type,
                name = achievement.name,
                description = achievement.description,
                iconUrl = achievement.iconUrl,
                progress = achievement.progress,
                target = achievement.target,
                isUnlocked = achievement.isUnlocked,
                unlockedAt = achievement.unlockedAt,
                rewardPoints = achievement.rewardPoints
            )
        }
    }
}