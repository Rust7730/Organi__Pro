package com.e243768.organipro_.domain.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserStatsRepository {

    // Get stats
    suspend fun getUserStats(userId: String): Result<UserStats>
    fun getUserStatsFlow(userId: String): Flow<UserStats?>

    // Update stats
    suspend fun updateUserStats(stats: UserStats): Result<Unit>
    suspend fun incrementTasksCompleted(userId: String): Result<Unit>
    suspend fun updateTasksCompletedToday(userId: String, count: Int): Result<Unit>
    suspend fun updateStreak(userId: String, streak: Int): Result<Unit>
    suspend fun addPoints(userId: String, points: Int): Result<Unit>

    // Calculate stats
    suspend fun recalculateStats(userId: String): Result<UserStats>
    suspend fun updateWeeklyStats(userId: String): Result<Unit>
    suspend fun updateMonthlyStats(userId: String): Result<Unit>
    suspend fun resetDailyStats(userId: String): Result<Unit>

    // Remote sync
    suspend fun fetchStatsFromRemote(userId: String): Result<UserStats>
    suspend fun syncStats(userId: String): Result<Unit>
}