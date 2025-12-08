package com.e243768.organipro_.presentation.viewmodels.tasks.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.core.util.DateUtils
import com.e243768.organipro_.domain.repository.AttachmentRepository
import com.e243768.organipro_.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val attachmentRepository: AttachmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Recuperamos el ID pasado por la navegación
    private val taskId: String = savedStateHandle.get<String>("taskId") ?: ""

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        loadTaskDetail()
    }

    fun onEvent(event: TaskDetailUiEvent) {
        when (event) {
            is TaskDetailUiEvent.BackClicked -> handleBackClick()
            is TaskDetailUiEvent.EditClicked -> handleEditClick()
            is TaskDetailUiEvent.DeleteClicked -> handleDeleteClick()
            is TaskDetailUiEvent.AttachmentClicked -> handleAttachmentClick(event.attachmentId)
            is TaskDetailUiEvent.MarkAsCompleted -> handleMarkAsCompleted()
        }
    }

    private fun loadTaskDetail() {
        if (taskId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "ID de tarea inválido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Observamos el flujo para tener actualizaciones en tiempo real
            taskRepository.getTaskByIdFlow(taskId).collect { task ->
                if (task != null) {
                    _uiState.update { state ->
                        state.copy(
                            taskId = task.id,
                            title = task.title,
                            description = task.description,
                            priority = task.priority.displayName,
                            points = task.getFormattedPoints(),
                            dueDate = task.dueDate?.let { DateUtils.formatDisplayDate(it) } ?: "Sin fecha",
                            attachments = task.attachments,
                            isCompleted = task.isCompleted(),
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "La tarea no existe o fue eliminada")
                    }
                }
            }
        }
    }

    private fun handleBackClick() {
        _navigationEvent.value = NavigationEvent.NavigateBack
    }

    private fun handleEditClick() {
        // TODO: Navegar a pantalla de edición (Feature futura)
        println("Edit task: $taskId")
    }

    private fun handleDeleteClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = taskRepository.deleteTask(taskId)

            when (result) {
                is Result.Success -> {
                    // Si se borra con éxito, volvemos atrás
                    _navigationEvent.value = NavigationEvent.NavigateBack
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Error al eliminar: ${result.message}")
                    }
                }
                else -> {}
            }
        }
    }

    private fun handleAttachmentClick(attachmentId: String) {
        viewModelScope.launch {
            val result = attachmentRepository.getAttachmentById(attachmentId)
            if (result is Result.Success) {
                // Aquí podríamos iniciar la descarga o abrir el visualizador
                val attachment = result.data
                println("Open attachment: ${attachment.name} (${attachment.url})")
                // TODO: Implementar lógica de visualización de archivos (Intent o DownloadManager)
            }
        }
    }

    private fun handleMarkAsCompleted() {
        viewModelScope.launch {
            val isCurrentlyCompleted = _uiState.value.isCompleted

            val result = if (isCurrentlyCompleted) {
                taskRepository.markTaskAsInProgress(taskId)
            } else {
                taskRepository.markTaskAsCompleted(taskId)
            }

            if (result is Result.Error) {
                _uiState.update { it.copy(error = "Error al actualizar estado: ${result.message}") }
            }
            // El éxito no necesita acción manual porque estamos observando el Flow en loadTaskDetail
        }
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
    }
}