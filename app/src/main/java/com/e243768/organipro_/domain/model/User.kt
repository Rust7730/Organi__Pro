package com.e243768.organipro_.domain.model

import java.util.Date

data class User(
    val id: String = "",
    val name: String = "",
    val alias: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val level: Int = 1,
    val currentXP: Int = 0,
    val totalPoints: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val tasksCompleted: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val lastLoginAt: Date? = null,
    val isActive: Boolean = true
) {
    /**
     * Calcula el XP necesario para el siguiente nivel
     */
    fun getXPForNextLevel(): Int = level * 1000

    /**
     * Calcula el XP actual dentro del nivel
     */
    fun getCurrentLevelXP(): Int = currentXP % 1000

    /**
     * Calcula el progreso del nivel (0.0 a 1.0)
     */
    fun getLevelProgress(): Float {
        val xpInLevel = getCurrentLevelXP()
        val xpNeeded = getXPForNextLevel()
        return if (xpNeeded > 0) xpInLevel.toFloat() / xpNeeded.toFloat() else 0f
    }

    /**
     * Verifica si el usuario puede subir de nivel
     */
    fun canLevelUp(): Boolean = currentXP >= getXPForNextLevel()

    /**
     * Obtiene el nombre a mostrar (alias si existe, si no el nombre)
     */
    fun getDisplayName(): String = alias.ifEmpty { name }
}