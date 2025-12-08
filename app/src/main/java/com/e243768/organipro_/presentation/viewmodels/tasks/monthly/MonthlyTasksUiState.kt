package com.e243768.organipro_.presentation.viewmodels.tasks.monthly

import com.e243768.organipro_.domain.model.Task
import java.util.Date

data class MonthlyTasksUiState(
    val monthLabel: String = "", // Ej: "Octubre 2023"
    val days: List<MonthlyDayData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class MonthlyDayData(
    val date: Date?, // Null si es un espacio vacío (padding del calendario)
    val dayNumber: String = "",
    val hasTasks: Boolean = false,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val isToday: Boolean = false,
    val isSelected: Boolean = false
)