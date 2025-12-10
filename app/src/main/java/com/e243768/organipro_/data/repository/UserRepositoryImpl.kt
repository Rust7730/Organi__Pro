package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.constants.FirebaseConstants
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.core.util.PointsCalculator
import com.e243768.organipro_.data.local.dao.UserDao
import com.e243768.organipro_.data.local.entities.UserEntity
import com.e243768.organipro_.data.remote.cloudinary.CloudinaryService
import com.e243768.organipro_.data.remote.firebase.FirebaseAuthService
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.mappers.UserMapper
import com.e243768.organipro_.domain.model.User
import com.e243768.organipro_.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import android.net.Uri
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuthService: FirebaseAuthService,
    private val firestoreService: FirebaseFirestoreService,
    private val cloudinaryService: CloudinaryService, // <--- INYECTAR ESTO
) : UserRepository {

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            // Intentar obtener de local primero
            val userEntity = userDao.getUserById(userId)
            if (userEntity != null) {
                return Result.Success(userEntity.toDomain())
            }

            // Si no está en local, obtener de Firebase
            fetchUserFromRemote(userId)
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
            val userEntity = UserEntity.fromDomain(user, synced = false)
            userDao.insertUser(userEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al guardar usuario: ${e.message}", e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val updatedUser = user.copy(updatedAt = Date())
            val userEntity = UserEntity.fromDomain(updatedUser, synced = false)
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
        return try {
            val userMap = firestoreService.getDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId,
                clazz = Map::class.java
            ) as? Map<String, Any?> ?: return Result.Error("Usuario no encontrado")

            val user = UserMapper.fromFirebaseMap(userMap)

            // Guardar en local
            saveUser(user)

            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("Error al obtener usuario de Firebase: ${e.message}", e)
        }
    }

    override suspend fun syncUser(user: User): Result<Unit> {
        return try {
            // 1. Guardar en local
            saveUser(user)

            // 2. Subir a Firebase
            val userMap = UserMapper.toFirebaseMap(user)
            firestoreService.setDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = user.id,
                data = userMap,
                merge = true
            )

            // 3. Marcar como sincronizado
            userDao.updateSyncStatus(user.id, synced = true)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al sincronizar usuario: ${e.message}", e)
        }
    }

    override suspend fun syncAllUsers(): Result<Unit> {
        return try {
            val unsyncedUsers = userDao.getUnsyncedUsers()
            unsyncedUsers.forEach { entity ->
                val user = entity.toDomain()
                val userMap = UserMapper.toFirebaseMap(user)

                firestoreService.setDocument(
                    collection = FirebaseConstants.COLLECTION_USERS,
                    documentId = entity.id,
                    data = userMap,
                    merge = true
                )

                userDao.updateSyncStatus(entity.id, synced = true)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al sincronizar usuarios: ${e.message}", e)
        }
    }

    override suspend fun updateLevelAndXP(userId: String, level: Int, xp: Int): Result<Unit> {
        return try {
            // Actualizar local
            userDao.updateLevelAndXP(userId, level, xp)

            // Actualizar Firebase
            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId,
                updates = mapOf(
                    "level" to level,
                    "currentXP" to xp,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )

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
                updateLevelAndXP(userId, newLevel, newXP)

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
                val newXP = user.currentXP + xpToAdd
                val newLevel = PointsCalculator.calculateLevel(newXP)

                val updatedUser = user.copy(
                    totalPoints = newPoints,
                    currentXP = newXP,
                    level = newLevel,
                    updatedAt = Date()
                )

                updateUser(updatedUser)

                // Actualizar Firebase
                firestoreService.updateDocument(
                    collection = FirebaseConstants.COLLECTION_USERS,
                    documentId = userId,
                    updates = mapOf(
                        "totalPoints" to newPoints,
                        "currentXP" to newXP,
                        "level" to newLevel,
                        "updatedAt" to com.google.firebase.Timestamp.now()
                    )
                )

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

            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId,
                updates = mapOf(
                    "totalPoints" to points,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar puntos: ${e.message}", e)
        }
    }

    override suspend fun updateStreak(userId: String, streak: Int): Result<Unit> {
        return try {
            userDao.updateStreak(userId, streak)

            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId,
                updates = mapOf(
                    "currentStreak" to streak,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )

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

                firestoreService.updateDocument(
                    collection = FirebaseConstants.COLLECTION_USERS,
                    documentId = userId,
                    updates = mapOf(
                        "currentStreak" to newStreak,
                        "longestStreak" to newLongestStreak,
                        "updatedAt" to com.google.firebase.Timestamp.now()
                    )
                )

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
        val userId = firebaseAuthService.getCurrentUserId()
            ?: return Result.Error("Usuario no autenticado")
        return getUserById(userId)
    }

    override fun getCurrentUserFlow(): Flow<User?> {
        val userId = firebaseAuthService.getCurrentUserId()
            ?: throw IllegalStateException("Usuario no autenticado")
        return getUserByIdFlow(userId)
    }
    override suspend fun updateProfilePhoto(userId: String, imageUri: Uri): Result<String> {
        return try {
            // 1. Subir a Cloudinary
            val photoUrl = cloudinaryService.uploadImage(imageUri)
                ?: return Result.Error("Error al subir imagen a Cloudinary") // Corrección: String primero

            // 2. Actualizar en Firebase (Usando tu firestoreService existente)
// En UserRepositoryImpl.kt, dentro de updateProfilePhoto

            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId,
                updates = mapOf(
                    "avatarUrl" to photoUrl, // <--- CAMBIA "photoUrl" POR "avatarUrl" para coincidir con tu modelo User
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )
            // 3. Actualizar en Base de datos Local (DAO)
            userDao.updatePhotoUrl(userId, photoUrl)

            Result.Success(photoUrl)
        } catch (e: Exception) {
            // Corrección: Mensaje String primero, excepción después
            Result.Error("Error al actualizar foto: ${e.message}", e)
        }
    }}