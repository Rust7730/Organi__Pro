package com.e243768.organipro_.domain.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.User

interface AuthRepository {

    // Authentication
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(name: String, alias: String, email: String, password: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>

    // Session
    suspend fun getCurrentUserId(): String?
    suspend fun isUserLoggedIn(): Boolean
    suspend fun refreshSession(): Result<Unit>

    // Profile
    suspend fun updateProfile(name: String, alias: String): Result<Unit>
    suspend fun updateEmail(newEmail: String, password: String): Result<Unit>
    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit>
    suspend fun deleteAccount(password: String): Result<Unit>
}