package com.e243768.organipro_.presentation.viewmodels.tasks.detail

data class TaskDetailUiState(
    val taskId: String = "",
    val title: String = "",
    val description: String = "",
    val priority: String = "",
    val points: String = "",
    val dueDate: String = "",
    val attachments: List<Attachment> = emptyList(),
    val isCompleted: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class Attachment(
    val id: String,
    val name: String,
    val type: String, // "pdf", "image", "doc", etc.
    val size: String
)