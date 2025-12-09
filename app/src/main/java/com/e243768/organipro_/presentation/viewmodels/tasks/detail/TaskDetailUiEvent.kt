package com.e243768.organipro_.presentation.viewmodels.tasks.detail

sealed class TaskDetailUiEvent {
    object CompleteTaskClicked : TaskDetailUiEvent()
    object DeleteTaskClicked : TaskDetailUiEvent()
    object EditTaskClicked : TaskDetailUiEvent()
    object BackClicked : TaskDetailUiEvent()
    data class AttachmentClicked(val attachmentId: String) : TaskDetailUiEvent()
}