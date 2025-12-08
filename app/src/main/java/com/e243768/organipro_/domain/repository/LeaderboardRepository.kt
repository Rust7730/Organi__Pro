package com.e243768.organipro_.domain.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.UserRank
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {

    // Get rankings
    fun getAllRankings(): Flow<List<UserRank>>
    fun getWeeklyTopRankings(limit: Int = 10): Flow<List<UserRank>>
    fun getMonthlyTopRankings(limit: Int = 10): Flow<List<UserRank>>
    suspend fun getUserRank(userId: String): Result<UserRank>

    // Update rankings
    suspend fun updateLeaderboard(): Result<Unit>
    suspend fun fetchLeaderboardFromRemote(): Result<List<UserRank>>
    suspend fun clearLeaderboard(): Result<Unit>

    // Utility
    suspend fun updateUserRanking(userId: String): Result<UserRank>
}