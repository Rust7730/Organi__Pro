package com.e243768.organipro_.presentation.viewmodels.tasks.daily

import com.e243768.organipro_.domain.model.Task

sealed class DailyTasksUiEvent {
    data class TabSelected(val tab: TaskTab) : DailyTasksUiEvent()
    data class TaskClicked(val task: Task) : DailyTasksUiEvent()
    data class TimeSlotClicked(val time: String) : DailyTasksUiEvent()
    object BackClicked : DailyTasksUiEvent()
    object RefreshTasks : DailyTasksUiEvent()
}