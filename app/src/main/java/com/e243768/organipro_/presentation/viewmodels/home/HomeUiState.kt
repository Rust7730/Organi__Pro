package com.e243768.organipro_.presentation.viewmodels.home

import com.e243768.organipro_.domain.model.Task

data class HomeUiState(
    val userName: String = "",
    val userLevel: Int = 1,
    val streak: Int = 0,
    val avatarResId: Int = 0,
    val todayTasks: List<Task> = emptyList(),
    val weekTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)