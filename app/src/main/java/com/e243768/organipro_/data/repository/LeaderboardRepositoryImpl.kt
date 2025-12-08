package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.constants.FirebaseConstants
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.local.dao.UserRankDao
import com.e243768.organipro_.data.local.entities.UserRankEntity
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.mappers.UserRankMapper
import com.e243768.organipro_.domain.model.UserRank
import com.e243768.organipro_.domain.repository.LeaderboardRepository
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
            Result.Error("Error al obtener ranking: ${e.message}", e)
        }
    }

    override suspend fun updateLeaderboard(): Result<Unit> {
        return try {
            // 1. Obtener rankings de Firebase
            fetchLeaderboardFromRemote()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar leaderboard: ${e.message}", e)
        }
    }

    override suspend fun fetchLeaderboardFromRemote(): Result<List<UserRank>> {
        return try {
            val rankMaps = firestoreService.getDocuments(
                collection = FirebaseConstants.COLLECTION_LEADERBOARD,
                clazz = Map::class.java
            ) { query ->
                query.orderBy("points", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(100)
            } as List<Map<String, Any?>>

            val rankings = rankMaps.mapIndexed { index, map ->
                val rank = UserRankMapper.fromFirebaseMap(map)
                rank.copy(rank = index + 1)
            }

            // 2. Limpiar leaderboard local
            clearLeaderboard()

            // 3. Guardar en local
            val entities = rankings.map { UserRankEntity.fromDomain(it) }
            userRankDao.insertRankings(entities)

            Result.Success(rankings)
        } catch (e: Exception) {
            Result.Error("Error al obtener leaderboard de Firebase: ${e.message}", e)
        }
    }

    override suspend fun clearLeaderboard(): Result<Unit> {
        return try {
            userRankDao.clearLeaderboard()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al limpiar leaderboard: ${e.message}", e)
        }
    }

    override suspend fun updateUserRanking(userId: String): Result<UserRank> {
        return try {
            // Calcular ranking basado en puntos y actualizar
            // Esta es una operación que típicamente se haría en el backend
            Result.Error("No implementado - debe hacerse en backend")
        } catch (e: Exception) {
            Result.Error("Error al actualizar ranking: ${e.message}", e)
        }
    }
}