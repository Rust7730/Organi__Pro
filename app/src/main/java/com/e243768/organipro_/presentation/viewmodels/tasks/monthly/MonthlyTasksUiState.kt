package com.e243768.organipro_.presentation.viewmodels.tasks.monthly

import com.e243768.organipro_.presentation.viewmodels.tasks.daily.TaskTab

data class MonthlyTasksUiState(
    val selectedTab: TaskTab = TaskTab.MONTH,
    val currentMonth: String = "",
    val daysOfWeek: List<String> = emptyList(),
    val monthDays: List<MonthDayData> = emptyList(),
    val selectedDay: Int? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class MonthDayData(
    val dayNumber: Int,
    val hasTask: Boolean = false,
    val isCurrentDay: Boolean = false,
    val isSelected: Boolean = false,
    val taskCount: Int = 0
)