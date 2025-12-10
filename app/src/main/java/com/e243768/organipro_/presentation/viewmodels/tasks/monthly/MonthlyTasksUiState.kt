package com.e243768.organipro_.presentation.viewmodels.tasks.monthly

import com.e243768.organipro_.domain.model.Task
import java.util.Date

data class MonthlyTasksUiState(
    val selectedDate: Date = Date(), // <--- AGREGADO: Fecha seleccionada
    val monthLabel: String = "",
    val days: List<MonthlyDayData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class MonthlyDayData(
    val date: Date?,
    val dayNumber: String = "",
    val hasTasks: Boolean = false,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val tasks: List<Task> = emptyList(), // <--- AGREGADO: Lista de tareas para mostrar
    val isToday: Boolean = false,
    val isSelected: Boolean = false
)