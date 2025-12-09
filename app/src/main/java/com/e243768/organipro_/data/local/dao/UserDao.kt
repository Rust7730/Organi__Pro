// kotlin
package com.e243768.organipro_.data.local.dao

import androidx.room.*
import com.e243768.organipro_.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("UPDATE user SET avatar_url = :photoUrl WHERE id = :userId")
    suspend fun updatePhotoUrl(userId: String, photoUrl: String)

    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM user WHERE is_active = 1")
    fun getAllActiveUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM user WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("UPDATE user SET level = :level, xp = :xp WHERE id = :userId")
    suspend fun updateLevelAndXP(userId: String, level: Int, xp: Int)
    @Query("UPDATE user SET total_points = :points WHERE id = :userId")
    suspend fun updatePoints(userId: String, points: Int)

    @Query("UPDATE user SET streak = :streak WHERE id = :userId")
    suspend fun updateStreak(userId: String, streak: Int)
    @Query("UPDATE user SET synced = :synced WHERE id = :userId")
    suspend fun updateSyncStatus(userId: String, synced: Boolean)

    @Query("SELECT * FROM user WHERE synced = 0")
    suspend fun getUnsyncedUsers(): List<UserEntity>
}