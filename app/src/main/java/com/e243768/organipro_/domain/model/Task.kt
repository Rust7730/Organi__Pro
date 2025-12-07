package com.e243768.organipro_.domain.model

import java.util.Date
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIA,
    val status: TaskStatus = TaskStatus.PENDING,
    val points: Int = 100,
    val dueDate: Date? = null,
    val scheduledTime: String? = null, // Formato: "HH:mm"
    val estimatedDuration: Int = 60, // En minutos
    val progress: Float = 0f, // 0.0 a 1.0
    val attachments: List<Attachment> = emptyList(),
    val tags: List<String> = emptyList(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val completedAt: Date? = null,
    val isRecurring: Boolean = false,
    val recurringPattern: RecurringPattern? = null
) {
    /**
     * Verifica si la tarea está completada
     */
    fun isCompleted(): Boolean = status == TaskStatus.COMPLETED

    /**
     * Verifica si la tarea está vencida
     */
    fun isOverdue(): Boolean {
        if (dueDate == null || isCompleted()) return false
        return Date().after(dueDate)
    }

    /**
     * Verifica si la tarea es para hoy
     */
    fun isToday(): Boolean {
        if (dueDate == null) return false
        val today = Date()
        val taskDate = dueDate
        return today.date == taskDate.date &&
                today.month == taskDate.month &&
                today.year == taskDate.year
    }

    /**
     * Obtiene el color según la prioridad
     */
    fun getPriorityColor(): String {
        return when (priority) {
            Priority.ALTA -> "#FF6B6B"
            Priority.MEDIA -> "#FFC700"
            Priority.BAJA -> "#4CAF50"
        }
    }

    /**
     * Obtiene los puntos formateados
     */
    fun getFormattedPoints(): String = "${points}pts"

    /**
     * Calcula si la tarea está en progreso
     */
    fun isInProgress(): Boolean = progress > 0f && progress < 1f
}

data class RecurringPattern(
    val type: RecurringType = RecurringType.DAILY,
    val interval: Int = 1, // Cada cuántos días/semanas/meses
    val daysOfWeek: List<Int> = emptyList(), // 1=Lunes, 7=Domingo
    val endDate: Date? = null
)

enum class RecurringType {
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}