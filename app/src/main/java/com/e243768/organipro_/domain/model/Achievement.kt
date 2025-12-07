package com.e243768.organipro_.domain.model

import java.util.Date

data class Achievement(
    val id: String = "",
    val userId: String = "",
    val type: AchievementType = AchievementType.TASKS_COMPLETED,
    val name: String = "",
    val description: String = "",
    val iconUrl: String? = null,
    val progress: Int = 0,
    val target: Int = 100,
    val isUnlocked: Boolean = false,
    val unlockedAt: Date? = null,
    val rewardPoints: Int = 0
) {
    /**
     * Calcula el porcentaje de progreso
     */
    fun getProgressPercentage(): Int {
        return if (target > 0) {
            ((progress.toFloat() / target.toFloat()) * 100).toInt()
        } else 0
    }

    /**
     * Verifica si está completado
     */
    fun isCompleted(): Boolean = progress >= target

    /**
     * Obtiene el progreso como fracción (0.0 a 1.0)
     */
    fun getProgressFraction(): Float {
        return if (target > 0) {
            (progress.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        } else 0f
    }
}

enum class AchievementType(val displayName: String) {
    TASKS_COMPLETED("Tareas completadas"),
    STREAK_DAYS("Días de racha"),
    POINTS_EARNED("Puntos ganados"),
    LEVEL_REACHED("Nivel alcanzado"),
    HIGH_PRIORITY_COMPLETED("Tareas de alta prioridad"),
    PERFECT_WEEK("Semana perfecta"),
    PERFECT_MONTH("Mes perfecto"),
    EARLY_BIRD("Madrugador"),
    NIGHT_OWL("Búho nocturno"),
    SPEED_DEMON("Demonio de la velocidad");

    companion object {
        fun fromString(value: String): AchievementType {
            return values().find {
                it.name.equals(value, ignoreCase = true)
            } ?: TASKS_COMPLETED
        }
    }
}