package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.core.util.PointsCalculator
import com.e243768.organipro_.data.local.dao.UserDao
import com.e243768.organipro_.data.local.entities.UserEntity
import com.e243768.organipro_.domain.model.User
import com.e243768.organipro_.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class UserRepositoryImpl(
    private val userDao: UserDao,
    // private val firebaseFirestore: FirebaseFirestore // TODO: Agregar cuando tengamos Firebase
) : UserRepository {

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val userEntity = userDao.getUserById(userId)
            if (userEntity != null) {
                Result.Success(userEntity.toDomain())
            } else {
                Result.Error("Usuario no encontrado")
            }
        } catch (e: Exception) {
            Result.Error("Error al obtener usuario: ${e.message}", e)
        }
    }

    override fun getUserByIdFlow(userId: String): Flow<User?> {
        return userDao.getUserByIdFlow(userId).map { it?.toDomain() }
    }

    override suspend fun getUserByEmail(email: String): Result<User> {
        return try {
            val userEntity = userDao.getUserByEmail(email)
            if (userEntity != null) {
                Result.Success(userEntity.toDomain())
            } else {
                Result.Error("Usuario no encontrado")
            }
        } catch (e: Exception) {
            Result.Error("Error al obtener usuario: ${e.message}", e)
        }
    }

    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            val userEntity = UserEntity.Companion.fromDomain(user, synced = false)
            userDao.insertUser(userEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al guardar usuario: ${e.message}", e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val updatedUser = user.copy(updatedAt = Date())
            val userEntity = UserEntity.Companion.fromDomain(updatedUser, synced = false)
            userDao.updateUser(userEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar usuario: ${e.message}", e)
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            userDao.deleteUserById(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al eliminar usuario: ${e.message}", e)
        }
    }

    override suspend fun fetchUserFromRemote(userId: String): Result<User> {
        // TODO: Implementar cuando tengamos Firebase
        return Result.Error("Firebase no configurado aún")
    }

    override suspend fun syncUser(user: User): Result<Unit> {
        // TODO: Implementar cuando tengamos Firebase
        return try {
            // 1. Guardar en local
            saveUser(user)

            // 2. Subir a Firebase
            // firebaseFirestore.collection("users").document(user.id).set(user)

            // 3. Marcar como sincronizado
            userDao.updateSyncStatus(user.id, synced = true)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al sincronizar usuario: ${e.message}", e)
        }
    }

    override suspend fun syncAllUsers(): Result<Unit> {
        // TODO: Implementar cuando tengamos Firebase
        return try {
            val unsyncedUsers = userDao.getUnsyncedUsers()
            unsyncedUsers.forEach { entity ->
                // Subir a Firebase
                // firebaseFirestore.collection("users").document(entity.id).set(entity.toDomain())
                userDao.updateSyncStatus(entity.id, synced = true)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al sincronizar usuarios: ${e.message}", e)
        }
    }

    override suspend fun updateLevelAndXP(userId: String, level: Int, xp: Int): Result<Unit> {
        return try {
            userDao.updateLevelAndXP(userId, level, xp)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar nivel y XP: ${e.message}", e)
        }
    }

    override suspend fun addXP(userId: String, xp: Int): Result<User> {
        return try {
            val userResult = getUserById(userId)
            if (userResult is Result.Success) {
                val user = userResult.data
                val newXP = user.currentXP + xp
                val newLevel = PointsCalculator.calculateLevel(newXP)

                val updatedUser = user.copy(
                    currentXP = newXP,
                    level = newLevel,
                    updatedAt = Date()
                )

                updateUser(updatedUser)
                Result.Success(updatedUser)
            } else {
                Result.Error("Usuario no encontrado")
            }
        } catch (e: Exception) {
            Result.Error("Error al agregar XP: ${e.message}", e)
        }
    }

    override suspend fun addPoints(userId: String, points: Int): Result<User> {
        return try {
            val userResult = getUserById(userId)
            if (userResult is Result.Success) {
                val user = userResult.data
                val newPoints = user.totalPoints + points
                val xpToAdd = PointsCalculator.pointsToXP(points)

                val updatedUser = user.copy(
                    totalPoints = newPoints,
                    currentXP = user.currentXP + xpToAdd,
                    level = PointsCalculator.calculateLevel(user.currentXP + xpToAdd),
                    updatedAt = Date()
                )

                updateUser(updatedUser)
                Result.Success(updatedUser)
            } else {
                Result.Error("Usuario no encontrado")
            }
        } catch (e: Exception) {
            Result.Error("Error al agregar puntos: ${e.message}", e)
        }
    }

    override suspend fun updatePoints(userId: String, points: Int): Result<Unit> {
        return try {
            userDao.updatePoints(userId, points)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar puntos: ${e.message}", e)
        }
    }

    override suspend fun updateStreak(userId: String, streak: Int): Result<Unit> {
        return try {
            userDao.updateStreak(userId, streak)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar racha: ${e.message}", e)
        }
    }

    override suspend fun incrementStreak(userId: String): Result<User> {
        return try {
            val userResult = getUserById(userId)
            if (userResult is Result.Success) {
                val user = userResult.data
                val newStreak = user.currentStreak + 1
                val newLongestStreak = maxOf(newStreak, user.longestStreak)

                val updatedUser = user.copy(
                    currentStreak = newStreak,
                    longestStreak = newLongestStreak,
                    updatedAt = Date()
                )

                updateUser(updatedUser)
                Result.Success(updatedUser)
            } else {
                Result.Error("Usuario no encontrado")
            }
        } catch (e: Exception) {
            Result.Error("Error al incrementar racha: ${e.message}", e)
        }
    }

    override suspend fun resetStreak(userId: String): Result<Unit> {
        return updateStreak(userId, 0)
    }

    override suspend fun getCurrentUser(): Result<User> {
        // TODO: Obtener el ID del usuario actual desde Firebase Auth
        return Result.Error("Firebase Auth no configurado aún")
    }

    override fun getCurrentUserFlow(): Flow<User?> {
        // TODO: Obtener el ID del usuario actual desde Firebase Auth
        // return getUserByIdFlow(currentUserId)
        throw NotImplementedError("Firebase Auth no configurado aún")
    }
}