package com.e243768.organipro_.data.local.dao

import androidx.room.*
import com.e243768.organipro_.data.local.entities.UserRankEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRankDao {

    @Query("SELECT * FROM leaderboard ORDER BY rank ASC")
    fun getAllRankings(): Flow<List<UserRankEntity>>

    @Query("SELECT * FROM leaderboard ORDER BY weekly_points DESC LIMIT :limit")
    fun getWeeklyTopRankings(limit: Int = 10): Flow<List<UserRankEntity>>

    @Query("SELECT * FROM leaderboard ORDER BY monthly_points DESC LIMIT :limit")
    fun getMonthlyTopRankings(limit: Int = 10): Flow<List<UserRankEntity>>

    @Query("SELECT * FROM leaderboard WHERE user_id = :userId")
    suspend fun getUserRank(userId: String): UserRankEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRanking(ranking: UserRankEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRankings(rankings: List<UserRankEntity>)

    @Update
    suspend fun updateRanking(ranking: UserRankEntity)

    @Query("DELETE FROM leaderboard")
    suspend fun clearLeaderboard()

    @Query("DELETE FROM leaderboard WHERE updated_at < :timestamp")
    suspend fun deleteOldRankings(timestamp: Long)

    @Query("UPDATE leaderboard SET is_current_user = :isCurrent WHERE user_id = :userId")
    suspend fun markAsCurrentUser(userId: String, isCurrent: Boolean)
}