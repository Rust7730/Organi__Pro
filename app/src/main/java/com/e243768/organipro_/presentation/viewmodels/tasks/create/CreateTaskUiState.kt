package com.e243768.organipro_.presentation.viewmodels.tasks.create

import com.e243768.organipro_.domain.model.Priority

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIA,
    val points: String = "100", // Puntos por defecto
    val scheduledTime: String = "", // Ej: "14:00"
    val scheduledDate: String = "", // Ej: "2023-10-27"
    val isLoading: Boolean = false,
    val error: String? = null,
    val isTaskSaved: Boolean = false
)