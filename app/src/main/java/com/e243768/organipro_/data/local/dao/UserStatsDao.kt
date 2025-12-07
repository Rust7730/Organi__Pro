package com.e243768.organipro_.data.local.dao

import androidx.room.*
import com.e243768.organipro_.data.local.entities.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {

    @Query("SELECT * FROM user_stats WHERE user_id = :userId")
    suspend fun getUserStats(userId: String): UserStatsEntity?

    @Query("SELECT * FROM user_stats WHERE user_id = :userId")
    fun getUserStatsFlow(userId: String): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStatsEntity)

    @Update
    suspend fun updateUserStats(stats: UserStatsEntity)

    @Query("DELETE FROM user_stats WHERE user_id = :userId")
    suspend fun deleteUserStats(userId: String)

    @Query("UPDATE user_stats SET tasks_completed_today = :count WHERE user_id = :userId")
    suspend fun updateTasksCompletedToday(userId: String, count: Int)

    @Query("UPDATE user_stats SET current_streak = :streak WHERE user_id = :userId")
    suspend fun updateStreak(userId: String, streak: Int)
}