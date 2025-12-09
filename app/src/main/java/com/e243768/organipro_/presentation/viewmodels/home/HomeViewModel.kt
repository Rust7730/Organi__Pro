package com.e243768.organipro_.presentation.viewmodels.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.Task
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.TaskRepository
import com.e243768.organipro_.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        loadData()
    }

    // ESTA ES LA FUNCIÓN CORRECTA (La única que debes tener)
    private fun handleTaskClick(task: Task) {
        _navigationEvent.value = NavigationEvent.NavigateToTaskDetail(task.id)
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.TaskClicked -> handleTaskClick(event.task)
            is HomeUiEvent.SettingsClicked -> handleSettingsClick()
            is HomeUiEvent.CreateTaskClicked -> handleCreateTaskClick()
            is HomeUiEvent.NavigationItemClicked -> handleNavigationClick(event.route)
            is HomeUiEvent.RefreshTasks -> refreshData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            // 1. Observar datos del usuario
            launch {
                userRepository.getUserByIdFlow(userId)
                    .catch { e ->
                        _uiState.update { it.copy(error = "Error cargando perfil: ${e.message}") }
                    }
                    .collect { user ->
                        if (user != null) {
                            _uiState.update { state ->
                                state.copy(
                                    userName = user.getDisplayName(),
                                    userLevel = user.level, // CORREGIDO: Se pasa el Int, la UI formatea
                                    streak = user.currentStreak,
                                    avatarResId = 0
                                )
                            }
                        }
                    }
            }

            // 2. Observar tareas de hoy
            launch {
                taskRepository.getTodayTasks(userId)
                    .catch { e ->
                        _uiState.update { it.copy(error = "Error cargando tareas: ${e.message}") }
                    }
                    .collect { tasks ->
                        _uiState.update { state ->
                            state.copy(
                                // FILTRO AGREGADO: Solo mostrar pendientes
                                todayTasks = tasks.filter { !it.isCompleted() },
                                isLoading = false
                            )
                        }
                    }
            }

            // 3. Observar tareas de la semana
            launch {
                taskRepository.getWeekTasks(userId)
                    .catch { } 
                    .collect { tasks ->
                        _uiState.update { state ->
                            // FILTRO AGREGADO: Pendientes y que no sean de hoy
                            state.copy(weekTasks = tasks.filter { !it.isCompleted() && !it.isToday() })
                        }
                    }
            }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = authRepository.getCurrentUserId() ?: return@launch

            // Forzar sync
            val userResult = userRepository.getUserById(userId)
            if (userResult is com.e243768.organipro_.core.result.Result.Success) {
                userRepository.syncUser(userResult.data)
            }
            taskRepository.syncAllTasks(userId)

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    // --- He eliminado la función duplicada handleTaskClick que estaba aquí ---

    private fun handleSettingsClick() {
        _navigationEvent.value = NavigationEvent.NavigateToSettings
    }

    private fun handleCreateTaskClick() {
        println("Create task clicked")
    }

    private fun handleNavigationClick(route: String) {
        _navigationEvent.value = NavigationEvent.NavigateToRoute(route)
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateToSettings : NavigationEvent()
        data class NavigateToRoute(val route: String) : NavigationEvent()
        data class NavigateToTaskDetail(val taskId: String) : NavigationEvent()
    }
}