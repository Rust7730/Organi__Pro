package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.local.dao.TaskDao
import com.e243768.organipro_.data.local.dao.UserStatsDao
import com.e243768.organipro_.data.local.entities.UserStatsEntity
import com.e243768.organipro_.domain.model.TaskStatus
import com.e243768.organipro_.domain.model.UserStats
import com.e243768.organipro_.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserStatsRepositoryImpl(
    private val userStatsDao: UserStatsDao,
    private val taskDao: TaskDao
) : UserStatsRepository {

    override suspend fun getUserStats(userId: String): Result<UserStats> {
        return try {
            val entity = userStatsDao.getUserStats(userId)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                // Crear stats por defecto si no existen
                val defaultStats = UserStats(userId = userId)
                val statsEntity = UserStatsEntity.fromDomain(defaultStats)
                userStatsDao.insertUserStats(statsEntity)
                Result.Success(defaultStats)
            }
        } catch (e: Exception) {
            Result.Error("Error al obtener estadísticas: ${e.message}", e)
        }
    }

    override fun getUserStatsFlow(userId: String): Flow<UserStats?> {
        return userStatsDao.getUserStatsFlow(userId).map { it?.toDomain() }
    }

    override suspend fun updateUserStats(stats: UserStats): Result<Unit> {
        return try {
            val entity = UserStatsEntity.fromDomain(stats)
            userStatsDao.updateUserStats(entity)
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
            userStatsDao.updateTasksCompletedToday(userId, count)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar tareas de hoy: ${e.message}", e)
        }
    }

    override suspend fun updateStreak(userId: String, streak: Int): Result<Unit> {
        return try {
            userStatsDao.updateStreak(userId, streak)
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

            // Aquí calcularíamos todas las estadísticas desde cero
            val statsResult = getUserStats(userId)
            if (statsResult is Result.Success) {
                val stats = statsResult.data.copy(
                    tasksCompleted = completedCount
                    // ... otros campos recalculados
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
                // Recalcular estadísticas de la semana
                val updatedStats = stats.copy(
                    weeklyPoints = 0, // Reset semanal
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
                    monthlyPoints = 0, // Reset mensual
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
        return try {
            updateTasksCompletedToday(userId, 0)
        } catch (e: Exception) {
            Result.Error("Error al resetear stats diarias: ${e.message}", e)
        }
    }

    override suspend fun fetchStatsFromRemote(userId: String): Result<UserStats> {
        // TODO: Implementar cuando tengamos Firebase
        return Result.Error("Firebase no configurado aún")
    }

    override suspend fun syncStats(userId: String): Result<Unit> {
        // TODO: Implementar cuando tengamos Firebase
        return Result.Error("Firebase no configurado aún")
    }
}