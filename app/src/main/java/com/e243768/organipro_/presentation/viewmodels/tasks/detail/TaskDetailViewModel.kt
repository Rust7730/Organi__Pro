package com.e243768.organipro_.presentation.viewmodels.tasks.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            delay(1000)

            // TODO: Cargar desde repository cuando esté implementado
            val mockTask = TaskDetailUiState(
                taskId = taskId,
                title = "Terminar los diseños de Figma",
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
                priority = "Alta",
                points = "200 pts.",
                dueDate = "11:59 p.m.",
                attachments = listOf(
                    Attachment(
                        id = "1",
                        name = "Diseño_final.pdf",
                        type = "pdf",
                        size = "2.5 MB"
                    ),
                    Attachment(
                        id = "2",
                        name = "Referencias.jpg",
                        type = "image",
                        size = "1.8 MB"
                    )
                ),
                isCompleted = false,
                isLoading = false
            )

            _uiState.value = mockTask
        }
    }

    private fun handleBackClick() {
        _navigationEvent.value = NavigationEvent.NavigateBack
    }

    private fun handleEditClick() {
        // TODO: Navegar a pantalla de edición
        println("Edit task: $taskId")
    }

    private fun handleDeleteClick() {
        // TODO: Mostrar diálogo de confirmación y eliminar
        println("Delete task: $taskId")
    }

    private fun handleAttachmentClick(attachmentId: String) {
        // TODO: Abrir archivo adjunto
        println("Open attachment: $attachmentId")
    }

    private fun handleMarkAsCompleted() {
        _uiState.update { it.copy(isCompleted = !it.isCompleted) }
        // TODO: Actualizar en repository
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
    }
}