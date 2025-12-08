package com.e243768.organipro_.presentation.viewmodels.tasks.create

import com.e243768.organipro_.domain.model.Priority

sealed class CreateTaskUiEvent {
    data class TitleChanged(val title: String) : CreateTaskUiEvent()
    data class DescriptionChanged(val description: String) : CreateTaskUiEvent()
    data class PrioritySelected(val priority: Priority) : CreateTaskUiEvent()
    data class PointsChanged(val points: String) : CreateTaskUiEvent()
    data class TimeChanged(val time: String) : CreateTaskUiEvent()
    object SaveClicked : CreateTaskUiEvent()
    object BackClicked : CreateTaskUiEvent()
}