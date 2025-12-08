package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.constants.FirebaseConstants
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.local.dao.AchievementDao
import com.e243768.organipro_.data.local.entities.AchievementEntity
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.mappers.AchievementMapper
import com.e243768.organipro_.domain.model.Achievement
import com.e243768.organipro_.domain.model.AchievementType
import com.e243768.organipro_.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao,
    private val firestoreService: FirebaseFirestoreService
) : AchievementRepository {

    override fun getAchievementsByUserId(userId: String): Flow<List<Achievement>> {
        return achievementDao.getAchievementsByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUnlockedAchievements(userId: String): Flow<List<Achievement>> {
        return achievementDao.getUnlockedAchievements(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getLockedAchievements(userId: String): Flow<List<Achievement>> {
        return achievementDao.getLockedAchievements(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAchievementByType(
        userId: String,
        type: AchievementType
    ): Result<Achievement> {
        return try {
            val entity = achievementDao.getAchievementByType(userId, type)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.Error("Logro no encontrado")
            }
        } catch (e: Exception) {
            Result.Error("Error al obtener logro: ${e.message}", e)
        }
    }

    override suspend fun updateAchievementProgress(
        achievementId: String,
        progress: Int
    ): Result<Unit> {
        return try {
            // 1. Actualizar local
            achievementDao.updateProgress(achievementId, progress)

            // 2. Actualizar Firebase
            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_ACHIEVEMENTS,
                documentId = achievementId,
                updates = mapOf("progress" to progress)
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar progreso: ${e.message}", e)
        }
    }

    override suspend fun unlockAchievement(achievementId: String): Result<Achievement> {
        return try {
            val unlockedAt = Date().time

            // 1. Actualizar local
            achievementDao.unlockAchievement(achievementId, unlockedAt)

            // 2. Actualizar Firebase
            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_ACHIEVEMENTS,
                documentId = achievementId,
                updates = mapOf(
                    "isUnlocked" to true,
                    "unlockedAt" to com.google.firebase.Timestamp(Date(unlockedAt))
                )
            )

            // 3. Obtener achievement actualizado (necesitaríamos getById)
            Result.Success(Achievement(
                id = achievementId,
                userId = "",
                type = AchievementType.TASKS_COMPLETED,
                name = "",
                description = "",
                iconUrl = null,
                progress = 0,
                target = 0,
                isUnlocked = true,
                unlockedAt = Date(unlockedAt),
                rewardPoints = 0
            ))
        } catch (e: Exception) {
            Result.Error("Error al desbloquear logro: ${e.message}", e)
        }
    }

    override suspend fun checkAndUnlockAchievements(userId: String): Result<List<Achievement>> {
        return try {
            // Esta lógica necesitaría acceso a UserStats
            Result.Success(emptyList())
        } catch (e: Exception) {
            Result.Error("Error al verificar logros: ${e.message}", e)
        }
    }

    override suspend fun initializeAchievements(userId: String): Result<Unit> {
        return try {
            val defaultAchievements = createDefaultAchievements(userId)

            // 1. Guardar en local
            val entities = defaultAchievements.map { AchievementEntity.fromDomain(it) }
            achievementDao.insertAchievements(entities)

            // 2. Guardar en Firebase
            defaultAchievements.forEach { achievement ->
                val achievementMap = AchievementMapper.toFirebaseMap(achievement)
                firestoreService.setDocument(
                    collection = FirebaseConstants.COLLECTION_ACHIEVEMENTS,
                    documentId = achievement.id,
                    data = achievementMap
                )
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al inicializar logros: ${e.message}", e)
        }
    }

    override suspend fun fetchAchievementsFromRemote(userId: String): Result<List<Achievement>> {
        return try {
            val achievementMaps = firestoreService.getDocuments(
                collection = FirebaseConstants.COLLECTION_ACHIEVEMENTS,
                clazz = Map::class.java
            ) { query ->
                query.whereEqualTo("userId", userId)
            } as List<Map<String, Any?>>

            val achievements = achievementMaps.map { AchievementMapper.fromFirebaseMap(it) }

            // Guardar en local
            val entities = achievements.map { AchievementEntity.fromDomain(it) }
            achievementDao.insertAchievements(entities)

            Result.Success(achievements)
        } catch (e: Exception) {
            Result.Error("Error al obtener logros de Firebase: ${e.message}", e)
        }
    }

    override suspend fun syncAchievements(userId: String): Result<Unit> {
        return try {
            fetchAchievementsFromRemote(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al sincronizar logros: ${e.message}", e)
        }
    }

    private fun createDefaultAchievements(userId: String): List<Achievement> {
        return listOf(
            Achievement(
                id = UUID.randomUUID().toString(),
                userId = userId,
                type = AchievementType.TASKS_COMPLETED,
                name = "Primera Tarea",
                description = "Completa tu primera tarea",
                iconUrl = null,
                progress = 0,
                target = 1,
                isUnlocked = false,
                unlockedAt = null,
                rewardPoints = 50
            ),
            Achievement(
                id = UUID.randomUUID().toString(),
                userId = userId,
                type = AchievementType.TASKS_COMPLETED,
                name = "10 Tareas",
                description = "Completa 10 tareas",
                iconUrl = null,
                progress = 0,
                target = 10,
                isUnlocked = false,
                unlockedAt = null,
                rewardPoints = 100
            ),
            Achievement(
                id = UUID.randomUUID().toString(),
                userId = userId,
                type = AchievementType.STREAK_DAYS,
                name = "Racha de 7 días",
                description = "Mantén una racha de 7 días",
                iconUrl = null,
                progress = 0,
                target = 7,
                isUnlocked = false,
                unlockedAt = null,
                rewardPoints = 150
            )
        )
    }
}