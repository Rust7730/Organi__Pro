package com.e243768.organipro_.presentation.viewmodels.tasks.daily

import com.e243768.organipro_.domain.model.Task

data class DailyTasksUiState(
    val selectedDate: String = "",
    val selectedTab: TaskTab = TaskTab.DAY,
    val timeSlots: List<TimeSlotData> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class TimeSlotData(
    val time: String,
    val task: Task? = null,
    val progress: Float = 0f
)

enum class TaskTab {
    DAY, WEEK, MONTH
}