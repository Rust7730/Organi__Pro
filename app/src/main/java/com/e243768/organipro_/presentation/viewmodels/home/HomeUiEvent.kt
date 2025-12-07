package com.e243768.organipro_.presentation.viewmodels.home

import com.e243768.organipro_.domain.model.Task

sealed class HomeUiEvent {  // ← Cambiar de "UiEvent" a "HomeUiEvent"
    data class TaskClicked(val task: Task) : HomeUiEvent()
    object SettingsClicked : HomeUiEvent()
    object CreateTaskClicked : HomeUiEvent()
    data class NavigationItemClicked(val route: String) : HomeUiEvent()
    object RefreshTasks : HomeUiEvent()
}