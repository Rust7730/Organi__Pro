package com.e243768.organipro_.presentation.viewmodels.tasks.create

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.Attachment
import com.e243768.organipro_.domain.model.AttachmentType
import com.e243768.organipro_.domain.model.Task
import com.e243768.organipro_.domain.model.TaskStatus
import com.e243768.organipro_.domain.repository.AttachmentRepository
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val attachmentRepository: AttachmentRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle // <--- Recibe el ID de navegación
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    // Si hay ID, estamos editando
    private val editingTaskId: String? = savedStateHandle.get<String>("taskId")

    init {
        if (editingTaskId != null) {
            loadTaskForEdit(editingTaskId)
        }
    }

    private fun loadTaskForEdit(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isEditing = true) }

            val taskResult = taskRepository.getTaskById(taskId)
            if (taskResult is Result.Success) {
                val task = taskResult.data

                // Cargar hora para la UI
                val timeInMillis = try {
                    val date = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(task.scheduledTime ?: "")
                    val cal = java.util.Calendar.getInstance()
                    val tCal = java.util.Calendar.getInstance().apply { time = date ?: Date() }
                    cal.set(java.util.Calendar.HOUR_OF_DAY, tCal.get(java.util.Calendar.HOUR_OF_DAY))
                    cal.set(java.util.Calendar.MINUTE, tCal.get(java.util.Calendar.MINUTE))
                    cal.timeInMillis
                } catch (e: Exception) { null }

                _uiState.update {
                    it.copy(
                        title = task.title,
                        description = task.description,
                        category = task.tags.firstOrNull() ?: "General",
                        priority = task.priority,
                        dueDate = task.dueDate,
                        dueTime = timeInMillis,
                        isLoading = false
                    )
                }
            }
        }
    }

    // ... (onEvent sigue igual) ...
    fun onEvent(event: CreateTaskUiEvent) {
        when (event) {
            is CreateTaskUiEvent.TitleChanged -> _uiState.update { it.copy(title = event.title) }
            is CreateTaskUiEvent.DescriptionChanged -> _uiState.update { it.copy(description = event.description) }
            is CreateTaskUiEvent.CategoryChanged -> _uiState.update { it.copy(category = event.category) }
            is CreateTaskUiEvent.PriorityChanged -> _uiState.update { it.copy(priority = event.priority) }
            is CreateTaskUiEvent.DateChanged -> _uiState.update { it.copy(dueDate = event.date) }
            is CreateTaskUiEvent.TimeChanged -> _uiState.update { it.copy(dueTime = event.time) }
            is CreateTaskUiEvent.AttachmentSelected -> {
                val list = _uiState.value.selectedAttachments.toMutableList().apply { add(event.uri) }
                _uiState.update { it.copy(selectedAttachments = list) }
            }
            is CreateTaskUiEvent.AttachmentRemoved -> {
                val list = _uiState.value.selectedAttachments.toMutableList().apply { remove(event.uri) }
                _uiState.update { it.copy(selectedAttachments = list) }
            }
            is CreateTaskUiEvent.SaveTaskClicked -> saveTask()
            is CreateTaskUiEvent.BackClicked -> _navigationEvent.value = NavigationEvent.NavigateBack
        }
    }

    private fun saveTask() {
        val currentState = _uiState.value
        if (currentState.title.isBlank()) {
            _uiState.update { it.copy(error = "Título obligatorio") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = authRepository.getCurrentUserId() ?: return@launch

            // Si editamos, usamos el ID existente. Si no, uno nuevo.
            val taskId = editingTaskId ?: UUID.randomUUID().toString()

            val formattedTime = currentState.dueTime?.let {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))
            }

            val task = Task(
                id = taskId,
                userId = userId,
                title = currentState.title.trim(),
                description = currentState.description.trim(),
                priority = currentState.priority,
                status = TaskStatus.PENDING,
                points = 100,
                dueDate = currentState.dueDate ?: Date(),
                scheduledTime = formattedTime,
                estimatedDuration = 60,
                tags = listOf(currentState.category),
                createdAt = if (editingTaskId != null) Date() else Date(), // Mantener original idealmente
                updatedAt = Date()
            )

            // Lógica Crear vs Actualizar
            val result = if (editingTaskId != null) {
                taskRepository.updateTask(task)
            } else {
                taskRepository.createTask(task)
            }

            if (result is Result.Success) {
                if (currentState.selectedAttachments.isNotEmpty()) {
                    saveAttachments(taskId, currentState.selectedAttachments)
                }
                _uiState.update { it.copy(isLoading = false) }
                _navigationEvent.value = NavigationEvent.NavigateBack
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al guardar") }
            }
        }
    }

    private suspend fun saveAttachments(taskId: String, uris: List<Uri>) {
        uris.forEach { uri ->
            val attachment = Attachment(
                id = UUID.randomUUID().toString(),
                taskId = taskId,
                name = uri.lastPathSegment ?: "Adjunto",
                type = AttachmentType.OTHER,
                mimeType = "application/octet-stream",
                size = 0,
                url = "",
                localPath = uri.toString(),
                uploadedAt = Date(),
                isUploaded = false
            )
            attachmentRepository.addAttachment(attachment)
        }
    }

    fun onNavigationHandled() { _navigationEvent.value = null }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
    }
}