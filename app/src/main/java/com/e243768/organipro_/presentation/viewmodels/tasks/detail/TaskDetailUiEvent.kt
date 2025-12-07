package com.e243768.organipro_.presentation.viewmodels.tasks.detail

sealed class TaskDetailUiEvent {
    object BackClicked : TaskDetailUiEvent()
    object EditClicked : TaskDetailUiEvent()
    object DeleteClicked : TaskDetailUiEvent()
    data class AttachmentClicked(val attachmentId: String) : TaskDetailUiEvent()
    object MarkAsCompleted : TaskDetailUiEvent()
}