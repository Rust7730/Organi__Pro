package com.e243768.organipro_.presentation.viewmodels.tasks.weekly

import com.e243768.organipro_.domain.model.Task
import com.e243768.organipro_.presentation.viewmodels.tasks.daily.TaskTab

sealed class WeeklyTasksUiEvent {
    data class TabSelected(val tab: TaskTab) : WeeklyTasksUiEvent()
    data class TaskClicked(val task: Task) : WeeklyTasksUiEvent()
    data class DaySlotClicked(val day: String, val time: String) : WeeklyTasksUiEvent()
    object BackClicked : WeeklyTasksUiEvent()
    object RefreshTasks : WeeklyTasksUiEvent()
}