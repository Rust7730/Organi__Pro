package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.constants.FirebaseConstants
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.local.dao.TaskDao
import com.e243768.organipro_.data.local.dao.UserStatsDao
import com.e243768.organipro_.data.local.entities.UserStatsEntity
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.mappers.UserStatsMapper
import com.e243768.organipro_.domain.model.UserStats
import com.e243768.organipro_.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserStatsRepositoryImpl @Inject constructor(
    private val userStatsDao: UserStatsDao,
    private val taskDao: TaskDao,
    private val firestoreService: FirebaseFirestoreService
) : UserStatsRepository {

    override suspend fun getUserStats(userId: String): Result<UserStats> {
        return try {
            // Intentar obtener de local primero
            val entity = userStatsDao.getUserStats(userId)
            if (entity != null) {
                return Result.Success(entity.toDomain())
            }

            // Si no existe, obtener de Firebase
            fetchStatsFromRemote(userId)
        } catch (e: Exception) {
            Result.Error("Error al obtener estadísticas: ${e.message}", e)
        }
    }

    override fun getUserStatsFlow(userId: String): Flow<UserStats?> {
        return userStatsDao.getUserStatsFlow(userId).map { it?.toDomain() }
    }

    override suspend fun updateUserStats(stats: UserStats): Result<Unit> {
        return try {
            // 1. Actualizar local
            val entity = UserStatsEntity.fromDomain(stats)
            userStatsDao.updateUserStats(entity)

            // 2. Actualizar Firebase
            val statsMap = UserStatsMapper.toFirebaseMap(stats)
            firestoreService.setDocument(
                collection = FirebaseConstants.COLLECTION_USER_STATS,
                documentId = stats.userId,
                data = statsMap,
                merge = true
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar estadísticas: ${e.message}", e)
        }
    }

    override suspend fun incrementTasksCompleted(userId: String): Result<Unit> {
        return try {
            val statsResult = getUserStats(userId)
            if (statsResult is Result.Success) {
                val stats = statsResult.data
                val updatedStats = stats.copy(
                    tasksCompleted = stats.tasksCompleted + 1,
                    tasksCompletedToday = stats.tasksCompletedToday + 1,
                    tasksCompletedThisWeek = stats.tasksCompletedThisWeek + 1,
                    tasksCompletedThisMonth = stats.tasksCompletedThisMonth + 1
                )
                updateUserStats(updatedStats)
            } else {
                Result.Error("Estadísticas no encontradas")
            }
        } catch (e: Exception) {
            Result.Error("Error al incrementar tareas completadas: ${e.message}", e)
        }
    }

    override suspend fun updateTasksCompletedToday(userId: String, count: Int): Result<Unit> {
        return try {
            // 1. Actualizar local
            userStatsDao.updateTasksCompletedToday(userId, count)

            // 2. Actualizar Firebase
            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_USER_STATS,
                documentId = userId,
                updates = mapOf("tasksCompletedToday" to count)
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar tareas de hoy: ${e.message}", e)
        }
    }

    override suspend fun updateStreak(userId: String, streak: Int): Result<Unit> {
        return try {
            // 1. Actualizar local
            userStatsDao.updateStreak(userId, streak)

            // 2. Actualizar Firebase
            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_USER_STATS,
                documentId = userId,
                updates = mapOf("currentStreak" to streak)
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar racha: ${e.message}", e)
        }
    }

    override suspend fun addPoints(userId: String, points: Int): Result<Unit> {
        return try {
            val statsResult = getUserStats(userId)
            if (statsResult is Result.Success) {
                val stats = statsResult.data
                val updatedStats = stats.copy(
                    totalPoints = stats.totalPoints + points,
                    weeklyPoints = stats.weeklyPoints + points,
                    monthlyPoints = stats.monthlyPoints + points
                )
                updateUserStats(updatedStats)
            } else {
                Result.Error("Estadísticas no encontradas")
            }
        } catch (e: Exception) {
            Result.Error("Error al agregar puntos: ${e.message}", e)
        }
    }

    override suspend fun recalculateStats(userId: String): Result<UserStats> {
        return try {
            val completedCount = taskDao.getCompletedTasksCount(userId)

            val statsResult = getUserStats(userId)
            if (statsResult is Result.Success) {
                val stats = statsResult.data.copy(
                    tasksCompleted = completedCount
                )
                updateUserStats(stats)
                Result.Success(stats)
            } else {
                Result.Error("Error al recalcular estadísticas")
            }
        } catch (e: Exception) {
            Result.Error("Error al recalcular estadísticas: ${e.message}", e)
        }
    }

    override suspend fun updateWeeklyStats(userId: String): Result<Unit> {
        return try {
            val statsResult = getUserStats(userId)
            if (statsResult is Result.Success) {
                val stats = statsResult.data
                val updatedStats = stats.copy(
                    weeklyPoints = 0,
                    tasksCompletedThisWeek = 0
                )
                updateUserStats(updatedStats)
            } else {
                Result.Error("Estadísticas no encontradas")
            }
        } catch (e: Exception) {
            Result.Error("Error al actualizar stats semanales: ${e.message}", e)
        }
    }

    override suspend fun updateMonthlyStats(userId: String): Result<Unit> {
        return try {
            val statsResult = getUserStats(userId)
            if (statsResult is Result.Success) {
                val stats = statsResult.data
                val updatedStats = stats.copy(
                    monthlyPoints = 0,
                    tasksCompletedThisMonth = 0
                )
                updateUserStats(updatedStats)
            } else {
                Result.Error("Estadísticas no encontradas")
            }
        } catch (e: Exception) {
            Result.Error("Error al actualizar stats mensuales: ${e.message}", e)
        }
    }

    override suspend fun resetDailyStats(userId: String): Result<Unit> {
        return updateTasksCompletedToday(userId, 0)
    }

    override suspend fun fetchStatsFromRemote(userId: String): Result<UserStats> {
        return try {
            val statsMap = firestoreService.getDocument(
                collection = FirebaseConstants.COLLECTION_USER_STATS,
                documentId = userId,
                clazz = Map::class.java
            ) as? Map<String, Any?>

            if (statsMap != null) {
                val stats = UserStatsMapper.fromFirebaseMap(statsMap)

                // Guardar en local
                val entity = UserStatsEntity.fromDomain(stats)
                userStatsDao.insertUserStats(entity)

                Result.Success(stats)
            } else {
                // Crear stats por defecto
                val defaultStats = UserStats(userId = userId)
                val entity = UserStatsEntity.fromDomain(defaultStats)
                userStatsDao.insertUserStats(entity)

                // Guardar en Firebase
                val statsMap = UserStatsMapper.toFirebaseMap(defaultStats)
                firestoreService.setDocument(
                    collection = FirebaseConstants.COLLECTION_USER_STATS,
                    documentId = userId,
                    data = statsMap
                )

                Result.Success(defaultStats)
            }
        } catch (e: Exception) {
            Result.Error("Error al obtener estadísticas de Firebase: ${e.message}", e)
        }
    }

    override suspend fun syncStats(userId: String): Result<Unit> {
        return try {
            val statsResult = getUserStats(userId)
            if (statsResult is Result.Success) {
                val statsMap = UserStatsMapper.toFirebaseMap(statsResult.data)
                firestoreService.setDocument(
                    collection = FirebaseConstants.COLLECTION_USER_STATS,
                    documentId = userId,
                    data = statsMap,
                    merge = true
                )
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al sincronizar estadísticas: ${e.message}", e)
        }
    }
}