package com.e243768.organipro_.presentation.viewmodels.tasks.monthly

import com.e243768.organipro_.presentation.viewmodels.tasks.daily.TaskTab

sealed class MonthlyTasksUiEvent {
    data class TabSelected(val tab: TaskTab) : MonthlyTasksUiEvent()
    data class DayClicked(val day: Int) : MonthlyTasksUiEvent()
    object BackClicked : MonthlyTasksUiEvent()
    object RefreshTasks : MonthlyTasksUiEvent()
    object PreviousMonth : MonthlyTasksUiEvent()
    object NextMonth : MonthlyTasksUiEvent()
}