package com.e243768.organipro_.presentation.viewmodels.tasks.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.domain.model.Task
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DailyTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyTasksUiState())
    val uiState: StateFlow<DailyTasksUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    private var tasksJob: Job? = null

    init {
        loadDailyTasks()
    }

    fun onEvent(event: DailyTasksUiEvent) {
        when (event) {
            is DailyTasksUiEvent.TabSelected -> handleTabSelection(event.tab)
            is DailyTasksUiEvent.TaskClicked -> handleTaskClick(event.task)
            is DailyTasksUiEvent.TimeSlotClicked -> handleTimeSlotClick(event.time)
            is DailyTasksUiEvent.BackClicked -> handleBackClick()
            is DailyTasksUiEvent.RefreshTasks -> loadDailyTasks()
            is DailyTasksUiEvent.NavigationItemClicked -> { /* Manejar si es necesario */ }
        }
    }

    private fun loadDailyTasks() {
        // Cancelamos cualquier job anterior para evitar conflictos si se recarga rápido
        tasksJob?.cancel()

        tasksJob = viewModelScope.launch {
            // 1. Obtener ID del usuario de forma segura dentro de la corrutina
            val userId = authRepository.getCurrentUserId()

            if (userId == null) {
                // Si no hay usuario, detenemos la carga (la UI debería redirigir al login)
                _uiState.update { it.copy(isLoading = false, error = "Usuario no identificado") }
                return@launch
            }

            // 2. Actualizar estado de carga y fecha
            val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
            val currentDate = dateFormat.format(Date())

            _uiState.update {
                it.copy(
                    isLoading = true,
                    selectedDate = currentDate,
                    error = null
                )
            }

            // 3. Recolectar el flujo de tareas desde el repositorio
            // .collect mantiene la suscripción activa a cambios en la base de datos
            taskRepository.getTodayTasks(userId).collect { tasks ->
                val timeSlots = mapTasksToTimeSlots(tasks)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        timeSlots = timeSlots
                    )
                }
            }
        }
    }

    private fun mapTasksToTimeSlots(tasks: List<Task>): List<TimeSlotData> {
        val slots = mutableListOf<TimeSlotData>()
        val startHour = 6
        val endHour = 22

        for (hour in startHour..endHour) {
            val timeLabel = formatHour(hour)

            // Lógica simple para coincidir hora (se puede mejorar con Dates reales)
            // Asumimos formato "HH:mm" en scheduledTime, ej: "14:00"
            val hourPrefix = String.format("%02d", hour)

            // Filtrar tareas no completadas y con scheduledTime
            val taskForSlot = tasks
                .filter { task -> task.status != com.e243768.organipro_.domain.model.TaskStatus.COMPLETED }
                .find { task ->
                    val st = task.scheduledTime
                    st != null && st.startsWith(hourPrefix)
                }

            println("[DailyTasksVM] slot=$hourPrefix task=${taskForSlot?.id ?: "-"}")

            slots.add(
                TimeSlotData(
                    time = timeLabel,
                    task = taskForSlot,
                    progress = taskForSlot?.progress ?: 0f
                )
            )
        }
        return slots
    }

    private fun formatHour(hour: Int): String {
        return when {
            hour == 0 -> "12:00 a.m."
            hour < 12 -> "$hour:00 a.m."
            hour == 12 -> "12:00 p.m."
            else -> "${hour - 12}:00 p.m."
        }
    }

    private fun handleTabSelection(tab: TaskTab) {
        _uiState.update { it.copy(selectedTab = tab) }

        when (tab) {
            TaskTab.DAY -> loadDailyTasks()
            TaskTab.WEEK -> _navigationEvent.value = NavigationEvent.NavigateToWeekly
            TaskTab.MONTH -> _navigationEvent.value = NavigationEvent.NavigateToMonthly
        }
    }

    private fun handleTaskClick(task: Task) {
        _navigationEvent.value = NavigationEvent.NavigateToTaskDetail(task.id)
    }

    private fun handleTimeSlotClick(time: String) {
        println("Time slot clicked: $time")
    }

    private fun handleBackClick() {
        _navigationEvent.value = NavigationEvent.NavigateBack
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
        object NavigateToWeekly : NavigationEvent()
        object NavigateToMonthly : NavigationEvent()
        data class NavigateToTaskDetail(val taskId: String) : NavigationEvent()
    }
}