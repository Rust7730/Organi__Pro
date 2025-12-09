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
            println("[LeaderboardRepo] fetchLeaderboardFromRemote: querying collection=${FirebaseConstants.COLLECTION_USER_STATS}")
            var statsRaw: List<Any> = emptyList()
            var statsList: List<Map<*, *>> = emptyList()
            try {
                val tmp = firestoreService.getDocuments(
                    collection = FirebaseConstants.COLLECTION_USER_STATS,
                    clazz = Map::class.java
                ) { query ->
                    query.orderBy("totalPoints", Query.Direction.DESCENDING).limit(20)
                }
                statsRaw = tmp as? List<Any> ?: emptyList()
                statsList = statsRaw.mapNotNull { it as? Map<*, *> }
                println("[LeaderboardRepo] user_stats returned count=${statsList.size}")
                if (statsList.isNotEmpty()) println("[LeaderboardRepo] sample=${statsList.take(3)}")
            } catch (e: Exception) {
                println("[LeaderboardRepo] error fetching user_stats: ${e.message}")
                statsList = emptyList()
            }

            if (statsList.isEmpty()) {
                try {
                    println("[LeaderboardRepo] user_stats empty; falling back to collection=${FirebaseConstants.COLLECTION_USERS}")
                    val usersRaw = firestoreService.getDocuments(
                        collection = FirebaseConstants.COLLECTION_USERS,
                        clazz = Map::class.java
                    ) { query ->
                        query.orderBy(FirebaseConstants.FIELD_USER_POINTS, Query.Direction.DESCENDING).limit(20)
                    }
                    val usersList = (usersRaw as? List<Any>)?.mapNotNull { it as? Map<*, *> } ?: emptyList()
                    println("[LeaderboardRepo] users fallback returned count=${usersList.size}")

                    // Convertimos usersList a la misma estructura que statsList para reutilizar el resto del flujo
                    statsList = usersList.map { userMap ->
                        mapOf(
                            "userId" to (userMap["id"] ?: userMap["userId"] ?: userMap["uid"] ?: userMap["uid"] ),
                            "totalPoints" to (userMap[FirebaseConstants.FIELD_USER_POINTS] ?: 0),
                            "weeklyPoints" to (userMap["weeklyPoints"] ?: 0),
                            "monthlyPoints" to (userMap["monthlyPoints"] ?: 0),
                            "currentStreak" to (userMap[FirebaseConstants.FIELD_USER_STREAK] ?: 0)
                        )
                    }
                } catch (e: Exception) {
                    // dejamos statsList vacío si falla el fallback
                    println("[LeaderboardRepo] error fetching users fallback: ${e.message}")
                }
            }

            val rankings = mutableListOf<UserRank>()

            // 2. Para cada estadística, obtener el perfil del usuario para saber su nombre
            statsList.forEachIndexed { index, statsMap ->
                val userId = (statsMap["userId"] ?: statsMap["user_id"])?.toString() ?: return@forEachIndexed
                val totalPoints = (statsMap["totalPoints"] as? Number)?.toInt()
                    ?: (statsMap["total_points"] as? Number)?.toInt() ?: 0
                val weeklyPoints = (statsMap["weeklyPoints"] as? Number)?.toInt()
                    ?: (statsMap["weekly_points"] as? Number)?.toInt() ?: 0
                val monthlyPoints = (statsMap["monthlyPoints"] as? Number)?.toInt()
                    ?: (statsMap["monthly_points"] as? Number)?.toInt() ?: 0
                val streak = (statsMap["currentStreak"] as? Number)?.toInt()
                    ?: (statsMap["current_streak"] as? Number)?.toInt() ?: 0

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
            println("[LeaderboardRepo] built rankings size=${rankings.size} sample=${rankings.take(5)}")
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