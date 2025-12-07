package com.e243768.organipro_.domain.model

data class UserStats(
    val userId: String = "",
    val totalPoints: Int = 0,
    val weeklyPoints: Int = 0,
    val monthlyPoints: Int = 0,
    val tasksCompleted: Int = 0,
    val tasksCompletedToday: Int = 0,
    val tasksCompletedThisWeek: Int = 0,
    val tasksCompletedThisMonth: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val averageTasksPerDay: Float = 0f,
    val completionRate: Float = 0f, // Porcentaje de tareas completadas
    val totalTasksCreated: Int = 0,
    val highPriorityCompleted: Int = 0,
    val mediumPriorityCompleted: Int = 0,
    val lowPriorityCompleted: Int = 0
) {
    /**
     * Calcula el porcentaje de finalización
     */
    fun getCompletionPercentage(): Int {
        return if (totalTasksCreated > 0) {
            ((tasksCompleted.toFloat() / totalTasksCreated.toFloat()) * 100).toInt()
        } else 0
    }

    /**
     * Obtiene el nivel de productividad basado en las estadísticas
     */
    fun getProductivityLevel(): ProductivityLevel {
        val avgTasks = averageTasksPerDay
        return when {
            avgTasks >= 10 -> ProductivityLevel.EXCELLENT
            avgTasks >= 7 -> ProductivityLevel.VERY_GOOD
            avgTasks >= 5 -> ProductivityLevel.GOOD
            avgTasks >= 3 -> ProductivityLevel.FAIR
            else -> ProductivityLevel.NEEDS_IMPROVEMENT
        }
    }
}

enum class ProductivityLevel(val displayName: String) {
    EXCELLENT("Excelente"),
    VERY_GOOD("Muy bueno"),
    GOOD("Bueno"),
    FAIR("Regular"),
    NEEDS_IMPROVEMENT("Necesita mejorar")
}