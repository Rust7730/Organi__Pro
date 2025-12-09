package com.e243768.organipro_.presentation.viewmodels.tasks.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.repository.AttachmentRepository // <--- IMPORTANTE
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.TaskRepository
import com.e243768.organipro_.domain.repository.UserRepository
import com.e243768.organipro_.domain.repository.UserStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userStatsRepository: UserStatsRepository,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository, // <--- INYECTAR REPO
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Cargar Tarea
            val taskResult = taskRepository.getTaskById(taskId)

            if (taskResult is Result.Success) {
                val task = taskResult.data

                // 2. Cargar Adjuntos
                val attachmentsResult = attachmentRepository.getAttachmentsByTaskId(taskId)
                val attachments = if (attachmentsResult is Result.Success) attachmentsResult.data else emptyList()

                // 3. Unir todo
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        task = task.copy(attachments = attachments)
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar") }
            }
        }
    }

    fun onEvent(event: TaskDetailUiEvent) {
        when (event) {
            is TaskDetailUiEvent.CompleteTaskClicked -> handleCompleteTask()
            is TaskDetailUiEvent.DeleteTaskClicked -> handleDeleteTask()
            is TaskDetailUiEvent.EditTaskClicked -> {
                _uiState.value.task?.let { task ->
                    _navigationEvent.value = NavigationEvent.NavigateToEdit(task.id)
                }
            }
            is TaskDetailUiEvent.BackClicked -> _navigationEvent.value = NavigationEvent.NavigateBack
            is TaskDetailUiEvent.AttachmentClicked -> handleAttachmentClick(event.attachmentId)
        }
    }

    private fun handleCompleteTask() {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = authRepository.getCurrentUserId() ?: return@launch

            if (taskRepository.markTaskAsCompleted(currentTask.id) is Result.Success) {
                userStatsRepository.incrementTasksCompleted(userId)
                userStatsRepository.addPoints(userId, 100)
                userRepository.addPoints(userId, 100)
                _navigationEvent.value = NavigationEvent.NavigateBack
            } else {
                _uiState.update { it.copy(error = "Error al completar") }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun handleDeleteTask() {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (taskRepository.deleteTask(currentTask.id) is Result.Success) {
                _navigationEvent.value = NavigationEvent.NavigateBack
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun handleAttachmentClick(attachmentId: String) {
        viewModelScope.launch {
            try {
                val res = attachmentRepository.getAttachmentById(attachmentId)
                if (res is Result.Success) {
                    var attachment = res.data

                    // Si no hay URL pero existe localPath, intentar subir
                    if (attachment.url.isBlank() && !attachment.localPath.isNullOrBlank()) {
                        val up = attachmentRepository.uploadAttachment(attachment, attachment.localPath!!)
                        if (up is Result.Success) {
                            attachment = up.data
                        } else if (up is Result.Error) {
                            // Mostrar mensaje específico y loggear excepción
                            val msg = up.message
                            _uiState.update { it.copy(error = "Error subiendo adjunto: ${msg}") }
                            up.exception?.printStackTrace()
                            return@launch
                        } else {
                            _uiState.update { it.copy(error = "Error subiendo adjunto") }
                            return@launch
                        }
                    }

                    if (attachment.url.isNotBlank()) {
                        _navigationEvent.value = NavigationEvent.NavigateOpenAttachment(attachment.url, attachment.mimeType)
                    } else {
                        _uiState.update { it.copy(error = "No se puede abrir el adjunto: sin URL") }
                    }
                } else {
                    _uiState.update { it.copy(error = "Adjunto no encontrado") }
                }
            } catch (e: Exception) {
                Log.e("TaskDetailVM", "Error al manejar attachment click", e)
                _uiState.update { it.copy(error = "Error al abrir adjunto: ${e.message}") }
            }
        }
    }

    fun onNavigationHandled() { _navigationEvent.value = null }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
        data class NavigateToEdit(val taskId: String) : NavigationEvent()
        data class NavigateOpenAttachment(val url: String, val mimeType: String?) : NavigationEvent()
    }
}