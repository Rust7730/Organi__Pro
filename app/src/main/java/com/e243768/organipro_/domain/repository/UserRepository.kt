package com.e243768.organipro_.domain.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    // Local operations
    suspend fun getUserById(userId: String): Result<User>
    fun getUserByIdFlow(userId: String): Flow<User?>
    suspend fun getUserByEmail(email: String): Result<User>
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun deleteUser(userId: String): Result<Unit>

    // Remote operations
    suspend fun fetchUserFromRemote(userId: String): Result<User>
    suspend fun syncUser(user: User): Result<Unit>
    suspend fun syncAllUsers(): Result<Unit>

    // Level & XP
    suspend fun updateLevelAndXP(userId: String, level: Int, xp: Int): Result<Unit>
    suspend fun addXP(userId: String, xp: Int): Result<User>

    // Points
    suspend fun addPoints(userId: String, points: Int): Result<User>
    suspend fun updatePoints(userId: String, points: Int): Result<Unit>

    // Streak
    suspend fun updateStreak(userId: String, streak: Int): Result<Unit>
    suspend fun incrementStreak(userId: String): Result<User>
    suspend fun resetStreak(userId: String): Result<Unit>

    // Utility
    suspend fun getCurrentUser(): Result<User>
    fun getCurrentUserFlow(): Flow<User?>
}