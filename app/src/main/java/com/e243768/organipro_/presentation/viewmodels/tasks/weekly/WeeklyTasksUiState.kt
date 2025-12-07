package com.e243768.organipro_.presentation.viewmodels.tasks.weekly

import com.e243768.organipro_.domain.model.Task
import com.e243768.organipro_.presentation.viewmodels.tasks.daily.TaskTab

data class WeeklyTasksUiState(
    val selectedTab: TaskTab = TaskTab.WEEK,
    val weekRange: String = "",
    val daysOfWeek: List<String> = emptyList(),
    val timeSlots: List<String> = emptyList(),
    val weeklyTasks: Map<String, Map<String, WeeklyTaskData>> = emptyMap(), // day -> time -> task
    val isLoading: Boolean = true,
    val error: String? = null
)

data class WeeklyTaskData(
    val task: Task? = null,
    val progress: Float = 0f
)