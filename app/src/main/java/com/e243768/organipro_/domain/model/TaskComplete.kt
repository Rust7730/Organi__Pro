package com.e243768.organipro_.domain.model

import java.util.Date

/**
 * Modelo extendido de Task con información agregada para vistas
 * que necesitan datos adicionales precalculados
 */
data class TaskComplete(
    val task: Task,
    val user: User? = null,
    val attachmentCount: Int = 0,
    val commentCount: Int = 0,
    val subtasksCount: Int = 0,
    val subtasksCompleted: Int = 0,
    val relatedTasksCount: Int = 0,
    val timeSpent: Int = 0, // En minutos
    val lastActivityAt: Date? = null
) {
    /**
     * Obtiene el porcentaje de subtareas completadas
     */
    fun getSubtasksProgress(): Float {
        return if (subtasksCount > 0) {
            subtasksCompleted.toFloat() / subtasksCount.toFloat()
        } else 0f
    }

    /**
     * Verifica si tiene archivos adjuntos
     */
    fun hasAttachments(): Boolean = attachmentCount > 0

    /**
     * Verifica si tiene comentarios
     */
    fun hasComments(): Boolean = commentCount > 0

    /**
     * Verifica si tiene subtareas
     */
    fun hasSubtasks(): Boolean = subtasksCount > 0

    /**
     * Calcula el tiempo restante hasta la fecha límite
     */
    fun getTimeRemaining(): String? {
        val dueDate = task.dueDate ?: return null
        val now = Date()

        if (now.after(dueDate)) return "Vencida"

        val diff = dueDate.time - now.time
        val hours = diff / (1000 * 60 * 60)
        val days = hours / 24

        return when {
            days > 1 -> "$days días"
            days == 1L -> "1 día"
            hours > 1 -> "$hours horas"
            else -> "Menos de 1 hora"
        }
    }
}