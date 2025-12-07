package com.e243768.organipro_.data.local.dao

import androidx.room.*
import com.e243768.organipro_.data.local.entities.AchievementEntity
import com.e243768.organipro_.domain.model.AchievementType
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements WHERE user_id = :userId")
    fun getAchievementsByUserId(userId: String): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE user_id = :userId AND is_unlocked = 1")
    fun getUnlockedAchievements(userId: String): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE user_id = :userId AND is_unlocked = 0")
    fun getLockedAchievements(userId: String): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE user_id = :userId AND type = :type")
    suspend fun getAchievementByType(userId: String, type: AchievementType): AchievementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("UPDATE achievements SET progress = :progress WHERE id = :achievementId")
    suspend fun updateProgress(achievementId: String, progress: Int)

    @Query("UPDATE achievements SET is_unlocked = 1, unlocked_at = :unlockedAt WHERE id = :achievementId")
    suspend fun unlockAchievement(achievementId: String, unlockedAt: Long)

    @Query("DELETE FROM achievements WHERE user_id = :userId")
    suspend fun deleteAchievementsByUserId(userId: String)
}