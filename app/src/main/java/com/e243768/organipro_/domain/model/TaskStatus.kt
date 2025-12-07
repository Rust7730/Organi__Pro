package com.e243768.organipro_.domain.model

enum class TaskStatus(val displayName: String) {
    PENDING("Pendiente"),
    IN_PROGRESS("En progreso"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada"),
    OVERDUE("Vencida");

    companion object {
        fun fromString(value: String): TaskStatus {
            return when (value.lowercase()) {
                "pending", "pendiente" -> PENDING
                "in_progress", "en progreso" -> IN_PROGRESS
                "completed", "completada" -> COMPLETED
                "cancelled", "cancelada" -> CANCELLED
                "overdue", "vencida" -> OVERDUE
                else -> PENDING
            }
        }
    }
}