package com.e243768.organipro_.domain.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.Achievement
import com.e243768.organipro_.domain.model.AchievementType
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {

    // Get achievements
    fun getAchievementsByUserId(userId: String): Flow<List<Achievement>>
    fun getUnlockedAchievements(userId: String): Flow<List<Achievement>>
    fun getLockedAchievements(userId: String): Flow<List<Achievement>>
    suspend fun getAchievementByType(userId: String, type: AchievementType): Result<Achievement>

    // Update achievements
    suspend fun updateAchievementProgress(achievementId: String, progress: Int): Result<Unit>
    suspend fun unlockAchievement(achievementId: String): Result<Achievement>
    suspend fun checkAndUnlockAchievements(userId: String): Result<List<Achievement>>

    // Initialize
    suspend fun initializeAchievements(userId: String): Result<Unit>

    // Remote sync
    suspend fun fetchAchievementsFromRemote(userId: String): Result<List<Achievement>>
    suspend fun syncAchievements(userId: String): Result<Unit>
}