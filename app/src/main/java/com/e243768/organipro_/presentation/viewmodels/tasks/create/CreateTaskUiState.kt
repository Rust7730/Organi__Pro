package com.e243768.organipro_.presentation.viewmodels.tasks.create

import android.net.Uri
import com.e243768.organipro_.domain.model.Attachment // <--- Importante
import com.e243768.organipro_.domain.model.Priority
import java.util.Date

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "General",
    val priority: Priority = Priority.MEDIA,
    val dueDate: Date? = null,
    val dueTime: Long? = null,

    // Archivos NUEVOS que el usuario selecciona ahora (Galería)
    val selectedAttachments: List<Uri> = emptyList(),

    // Archivos EXISTENTES que ya tenía la tarea (Base de datos)
    val existingAttachments: List<Attachment> = emptyList(),

    val isLoading: Boolean = false,
    val isEditing: Boolean = false, // Para cambiar el título de la pantalla
    val error: String? = null
)