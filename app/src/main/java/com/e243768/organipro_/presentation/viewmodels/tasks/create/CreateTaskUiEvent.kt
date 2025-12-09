package com.e243768.organipro_.presentation.viewmodels.tasks.create

import android.net.Uri
import com.e243768.organipro_.domain.model.Priority
import java.util.Date

sealed class CreateTaskUiEvent {
    data class TitleChanged(val title: String) : CreateTaskUiEvent()
    data class DescriptionChanged(val description: String) : CreateTaskUiEvent()
    data class CategoryChanged(val category: String) : CreateTaskUiEvent()
    data class PriorityChanged(val priority: Priority) : CreateTaskUiEvent()
    data class DateChanged(val date: Date) : CreateTaskUiEvent()
    data class TimeChanged(val time: Long) : CreateTaskUiEvent()

    // --- NUEVOS EVENTOS PARA ARCHIVOS ---
    data class AttachmentSelected(val uri: Uri) : CreateTaskUiEvent()
    data class AttachmentRemoved(val uri: Uri) : CreateTaskUiEvent()

    object SaveTaskClicked : CreateTaskUiEvent()
    object BackClicked : CreateTaskUiEvent()
}