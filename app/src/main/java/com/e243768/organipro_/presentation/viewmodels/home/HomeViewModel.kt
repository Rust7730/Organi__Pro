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
                // Si no hay usuario, podríamos navegar al login (manejado por el MainActivity/NavGraph usualmente)
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
                                    userLevel = "Lv.${user.level}",
                                    streak = user.currentStreak,
                                    avatarResId = 0 // TODO: Integrar carga de imagen real
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
                                todayTasks = tasks,
                                isLoading = false
                            )
                        }
                    }
            }

            // 3. Observar tareas de la semana
            launch {
                taskRepository.getWeekTasks(userId)
                    .catch { /* Manejar error silenciosamente o mostrar snackbar */ }
                    .collect { tasks ->
                        _uiState.update { state ->
                            // Filtramos las que no son de hoy para no duplicar visualmente si el diseño lo requiere
                            state.copy(weekTasks = tasks.filter { !it.isToday() })
                        }
                    }
            }
        }
    }

    private fun refreshData() {
        // Al usar Flows, los datos se actualizan solos si la DB cambia.
        // Si necesitamos forzar sincronización con la nube:
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = authRepository.getCurrentUserId() ?: return@launch

            // Forzar sync
            userRepository.syncUser(userRepository.getUserById(userId).getOrNull() ?: return@launch)
            taskRepository.syncAllTasks(userId)

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun handleTaskClick(task: Task) {
        // TODO: Navegar a detalle de tarea con ID real
        println("Task clicked: ${task.id}")
    }

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
    }
}