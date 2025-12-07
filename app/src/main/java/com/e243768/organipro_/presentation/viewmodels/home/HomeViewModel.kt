package com.e243768.organipro_.presentation.viewmodels.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.domain.model.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        loadUserProfile()
        loadTasks()
    }

    fun onEvent(event: HomeUiEvent) {  // ← Cambiar de "UiEvent" a "HomeUiEvent"
        when (event) {
            is HomeUiEvent.TaskClicked -> handleTaskClick(event.task)
            is HomeUiEvent.SettingsClicked -> handleSettingsClick()
            is HomeUiEvent.CreateTaskClicked -> handleCreateTaskClick()
            is HomeUiEvent.NavigationItemClicked -> handleNavigationClick(event.route)
            is HomeUiEvent.RefreshTasks -> loadTasks()
        }
    }

    private fun loadUserProfile() {
        // TODO: Cargar desde repository cuando esté implementado
        _uiState.update {
            it.copy(
                userName = "RustlessCar7730",
                userLevel = "Lv.20",
                streak = 20,
                avatarResId = 0 // TODO: Manejar avatar
            )
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simular carga de datos
            delay(1000)

            // TODO: Cargar desde repository cuando esté implementado
            val mockTasks = listOf(
                Task(
                    id = UUID.randomUUID().toString(),
                    title = "Terminar los diseños de Figma",
                    points = "200pts",
                    priority = "Alta",
                    time = "11:59 p.m.",
                    isToday = true
                ),
                Task(
                    id = UUID.randomUUID().toString(),
                    title = "Revisar PR de navegación",
                    points = "150pts",
                    priority = "Media",
                    time = "Mañana 10:00 a.m.",
                    isToday = false
                ),
                Task(
                    id = UUID.randomUUID().toString(),
                    title = "Implementar ViewModel de Perfil",
                    points = "100pts",
                    priority = "Baja",
                    time = "Viernes",
                    isToday = false
                )
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    todayTasks = mockTasks.filter { task -> task.isToday },
                    weekTasks = mockTasks.filter { task -> !task.isToday }
                )
            }
        }
    }

    private fun handleTaskClick(task: Task) {
        // TODO: Navegar a detalle de tarea cuando esté implementado
        println("Task clicked: ${task.title}")
    }

    private fun handleSettingsClick() {
        _navigationEvent.value = NavigationEvent.NavigateToSettings
    }

    private fun handleCreateTaskClick() {
        // TODO: Navegar a crear tarea cuando esté implementado
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