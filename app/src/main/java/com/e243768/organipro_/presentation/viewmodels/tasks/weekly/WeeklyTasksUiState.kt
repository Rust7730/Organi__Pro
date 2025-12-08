package com.e243768.organipro_.presentation.viewmodels.tasks.weekly

import com.e243768.organipro_.domain.model.Task
import java.util.Date

data class WeeklyTasksUiState(
    val selectedDate: Date = Date(), // La fecha seleccionada o actual
    val weekLabel: String = "",      // Ej: "Semana 24, Octubre"
    val days: List<WeeklyDayData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class WeeklyDayData(
    val date: Date,
    val dayName: String, // "Lun", "Mar"
    val dayNumber: String, // "12", "13"
    val tasks: List<Task> = emptyList(),
    val isToday: Boolean = false,
    val progress: Float = 0f // 0.0 a 1.0 (Completadas / Total)
)