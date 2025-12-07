package com.e243768.organipro_.core.util

import com.e243768.organipro_.core.constants.AppConstants

object PointsCalculator {

    /**
     * Calcula puntos según la prioridad
     */
    fun calculatePointsByPriority(priority: String): Int {
        return when (priority.lowercase()) {
            "alta" -> AppConstants.POINTS_HIGH_PRIORITY
            "media" -> AppConstants.POINTS_MEDIUM_PRIORITY
            "baja" -> AppConstants.POINTS_LOW_PRIORITY
            else -> AppConstants.POINTS_MEDIUM_PRIORITY
        }
    }

    /**
     * Calcula el bono de racha
     */
    fun calculateStreakBonus(streakDays: Int): Int {
        return streakDays * AppConstants.POINTS_STREAK_BONUS
    }

    /**
     * Calcula puntos totales con bono de racha
     */
    fun calculateTotalPoints(basePoints: Int, streakDays: Int): Int {
        val streakBonus = calculateStreakBonus(streakDays)
        return basePoints + streakBonus
    }

    /**
     * Calcula el nivel basado en XP
     */
    fun calculateLevel(xp: Int): Int {
        return (xp / AppConstants.XP_PER_LEVEL) + 1
    }

    /**
     * Calcula el XP necesario para el siguiente nivel
     */
    fun calculateXPForNextLevel(currentLevel: Int): Int {
        return currentLevel * AppConstants.XP_PER_LEVEL
    }

    /**
     * Calcula el XP actual dentro del nivel
     */
    fun calculateCurrentLevelXP(totalXP: Int): Int {
        return totalXP % AppConstants.XP_PER_LEVEL
    }

    /**
     * Calcula el progreso del nivel (0.0 a 1.0)
     */
    fun calculateLevelProgress(totalXP: Int): Float {
        val currentLevelXP = calculateCurrentLevelXP(totalXP)
        val xpForNextLevel = AppConstants.XP_PER_LEVEL
        return currentLevelXP.toFloat() / xpForNextLevel.toFloat()
    }

    /**
     * Convierte puntos a XP (1:1 ratio)
     */
    fun pointsToXP(points: Int): Int {
        return points
    }
}