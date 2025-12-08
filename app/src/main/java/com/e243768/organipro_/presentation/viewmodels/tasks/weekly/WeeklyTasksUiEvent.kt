package com.e243768.organipro_.presentation.viewmodels.tasks.weekly

import com.e243768.organipro_.domain.model.Task

sealed class WeeklyTasksUiEvent {
    data class TaskClicked(val task: Task) : WeeklyTasksUiEvent()
    data class DayClicked(val date: java.util.Date) : WeeklyTasksUiEvent()
    object PreviousWeekClicked : WeeklyTasksUiEvent()
    object NextWeekClicked : WeeklyTasksUiEvent()
    object BackClicked : WeeklyTasksUiEvent()
    object CreateTaskClicked : WeeklyTasksUiEvent()
}