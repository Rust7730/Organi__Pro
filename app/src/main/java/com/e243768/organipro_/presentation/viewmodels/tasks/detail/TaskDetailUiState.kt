package com.e243768.organipro_.presentation.viewmodels.tasks.detail

import com.e243768.organipro_.domain.model.Task

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)