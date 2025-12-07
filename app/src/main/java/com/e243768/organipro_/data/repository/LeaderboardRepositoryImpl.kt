package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.local.dao.UserRankDao
import com.e243768.organipro_.data.local.entities.UserRankEntity
import com.e243768.organipro_.domain.model.UserRank
import com.e243768.organipro_.domain.repository.LeaderboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LeaderboardRepositoryImpl(
    private val userRankDao: UserRankDao
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
            // TODO: Recalcular rankings desde Firebase y actualizar local
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar leaderboard: ${e.message}", e)
        }
    }

    override suspend fun fetchLeaderboardFromRemote(): Result<List<UserRank>> {
        // TODO: Implementar cuando tengamos Firebase
        return Result.Error("Firebase no configurado aún")
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
        // TODO: Calcular ranking basado en puntos y actualizar
        return Result.Error("No implementado aún")
    }
}