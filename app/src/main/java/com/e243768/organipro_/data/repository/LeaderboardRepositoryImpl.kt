package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.constants.FirebaseConstants
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.core.result.Result.*
import com.e243768.organipro_.data.local.dao.UserRankDao
import com.e243768.organipro_.data.local.entities.UserRankEntity
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.domain.model.UserRank
import com.e243768.organipro_.domain.repository.LeaderboardRepository
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LeaderboardRepositoryImpl @Inject constructor(
    private val userRankDao: UserRankDao,
    private val firestoreService: FirebaseFirestoreService
) : LeaderboardRepository {

    override fun getAllRankings(): Flow<List<UserRank>> {
        return userRankDao.getAllRankings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getWeeklyTopRankings(limit: Int): Flow<List<UserRank>> {
        return userRankDao.getWeeklyTopRankings(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getMonthlyTopRankings(limit: Int): Flow<List<UserRank>> {
        return userRankDao.getMonthlyTopRankings(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getUserRank(userId: String): Result<UserRank> {
        return try {
            val entity = userRankDao.getUserRank(userId)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.Error("Ranking no encontrado")
            }
        } catch (e: Exception) {
            Result.Error("Error al obtener ranking", e)
        }
    }

    override suspend fun updateLeaderboard(): Result<Unit> {
        return when (val fetchResult = fetchLeaderboardFromRemote()) {
            is Result.Success -> Success(Unit)
            is Result.Error -> Error(fetchResult.message, fetchResult.exception)
            Result.Loading -> TODO()
        }
    }

    override suspend fun fetchLeaderboardFromRemote(): Result<List<UserRank>> {
        return try {
            // 1. Obtener las mejores estadísticas (Top 20 por ejemplo)
            // Consultamos la colección user_stats ordenando por totalPoints (o weeklyPoints si prefieres)
            val statsList = firestoreService.getDocuments(
                collection = FirebaseConstants.COLLECTION_USER_STATS,
                clazz = Map::class.java
            ) { query ->
                query.orderBy("totalPoints", Query.Direction.DESCENDING).limit(20)
            } as List<Map<String, Any?>>

            val rankings = mutableListOf<UserRank>()

            // 2. Para cada estadística, obtener el perfil del usuario para saber su nombre
            statsList.forEachIndexed { index, statsMap ->
                val userId = statsMap["userId"] as? String ?: return@forEachIndexed
                val totalPoints = (statsMap["totalPoints"] as? Long)?.toInt() ?: 0
                val weeklyPoints = (statsMap["weeklyPoints"] as? Long)?.toInt() ?: 0
                val monthlyPoints = (statsMap["monthlyPoints"] as? Long)?.toInt() ?: 0
                val streak = (statsMap["currentStreak"] as? Long)?.toInt() ?: 0

                // Buscar datos del usuario (Nombre, Alias, Avatar)
                val userMap = try {
                    firestoreService.getDocument(
                        collection = FirebaseConstants.COLLECTION_USERS,
                        documentId = userId,
                        clazz = Map::class.java
                    ) as? Map<String, Any?>
                } catch (e: Exception) { null }

                val userName = userMap?.get("name") as? String ?: "Usuario"
                val userAlias = userMap?.get("alias") as? String ?: ""
                val avatarUrl = userMap?.get("avatarUrl") as? String

                // Construir el objeto UserRank
                rankings.add(
                    UserRank(
                        userId = userId,
                        userName = userName,
                        userAlias = userAlias,
                        avatarUrl = avatarUrl,
                        rank = index + 1,
                        points = totalPoints,
                        weeklyPoints = weeklyPoints,
                        monthlyPoints = monthlyPoints,
                        streak = streak,
                        level = 1 // Podrías calcularlo si lo tienes en el mapa
                    )
                )
            }

            // 3. Actualizar base de datos local
            userRankDao.clearLeaderboard() // Limpiamos tabla vieja
            val entities = rankings.map { UserRankEntity.fromDomain(it) }
            userRankDao.insertRankings(entities)

            Result.Success(rankings)
        } catch (e: Exception) {
            Result.Error("Error al sincronizar leaderboard: ${e.message}", e)
        }
    }

    override suspend fun clearLeaderboard(): Result<Unit> {
        return try {
            userRankDao.clearLeaderboard()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al limpiar", e)
        }
    }

    override suspend fun updateUserRanking(userId: String): Result<UserRank> {
        // En una app real, esto se hace en el backend. 
        // Aquí retornamos un stub o error controlado.
        return Result.Error("Operación gestionada por el servidor")
    }
}