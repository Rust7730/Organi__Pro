package com.e243768.organipro_.domain.model

data class TimeSlot(
    val time: String = "", // Formato: "HH:mm"
    val task: Task? = null,
    val isAvailable: Boolean = true,
    val dayOfWeek: Int? = null, // 1=Lunes, 7=Domingo
    val date: String? = null // Formato: "yyyy-MM-dd"
) {
    /**
     * Verifica si el slot tiene una tarea asignada
     */
    fun hasTask(): Boolean = task != null

    /**
     * Obtiene el progreso de la tarea (si existe)
     */
    fun getProgress(): Float = task?.progress ?: 0f

    /**
     * Verifica si la tarea está completada
     */
    fun isCompleted(): Boolean = task?.isCompleted() ?: false

    /**
     * Obtiene la hora en formato de visualización
     */
    fun getDisplayTime(): String {
        // Convertir de 24h a 12h si es necesario
        val parts = time.split(":")
        if (parts.size != 2) return time

        val hour = parts[0].toIntOrNull() ?: return time
        val minute = parts[1]

        return when {
            hour == 0 -> "12:$minute a.m."
            hour < 12 -> "$hour:$minute a.m."
            hour == 12 -> "12:$minute p.m."
            else -> "${hour - 12}:$minute p.m."
        }
    }
}