package com.e243768.organipro_.presentation.viewmodels.home

import com.e243768.organipro_.domain.model.Task

sealed class UiEvent {
    data class TaskClicked(val task: Task) : UiEvent()
    object SettingsClicked : UiEvent()
    object CreateTaskClicked : UiEvent()
    data class NavigationItemClicked(val route: String) : UiEvent()
    object RefreshTasks : UiEvent()
}