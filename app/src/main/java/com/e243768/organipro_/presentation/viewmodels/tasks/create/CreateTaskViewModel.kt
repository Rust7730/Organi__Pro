package com.e243768.organipro_.presentation.viewmodels.tasks.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.Task
import com.e243768.organipro_.domain.model.TaskStatus
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Recuperar hora pre-seleccionada si existe (ej: "14:00")
    private val initialTime: String? = savedStateHandle.get<String>("time")

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        if (!initialTime.isNullOrBlank()) {
            _uiState.update { it.copy(scheduledTime = initialTime) }
        }
    }

    fun onEvent(event: CreateTaskUiEvent) {
        when (event) {
            is CreateTaskUiEvent.TitleChanged -> _uiState.update { it.copy(title = event.title) }
            is CreateTaskUiEvent.DescriptionChanged -> _uiState.update { it.copy(description = event.description) }
            is CreateTaskUiEvent.PrioritySelected -> _uiState.update { it.copy(priority = event.priority) }
            is CreateTaskUiEvent.PointsChanged -> _uiState.update { it.copy(points = event.points) }
            is CreateTaskUiEvent.TimeChanged -> _uiState.update { it.copy(scheduledTime = event.time) }
            is CreateTaskUiEvent.SaveClicked -> saveTask()
            is CreateTaskUiEvent.BackClicked -> _navigationEvent.value = NavigationEvent.NavigateBack
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val userId = authRepository.getCurrentUserId()

            if (userId == null) {
                _uiState.update { it.copy(error = "Sesión inválida") }
                return@launch
            }

            if (currentState.title.isBlank()) {
                _uiState.update { it.copy(error = "El título es obligatorio") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, error = null) }

            val newTask = Task(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = currentState.title,
                description = currentState.description,
                priority = currentState.priority,
                points = currentState.points.toIntOrNull() ?: 100,
                status = TaskStatus.PENDING,
                createdAt = Date(),
                dueDate = Date(), // TODO: Implementar selector de fecha real
                scheduledTime = currentState.scheduledTime.ifBlank { null }
            )

            val result = taskRepository.createTask(newTask)

            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _navigationEvent.value = NavigationEvent.NavigateBack
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
    }
}