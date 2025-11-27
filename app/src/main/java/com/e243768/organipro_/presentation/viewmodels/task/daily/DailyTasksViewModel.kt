package com.e243768.organipro_.presentation.viewmodels.tasks.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.domain.model.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DailyTasksViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DailyTasksUiState())
    val uiState: StateFlow<DailyTasksUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

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
        }
    }

    private fun loadDailyTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simular carga de datos
            delay(1000)

            // Obtener fecha actual
            val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
            val currentDate = dateFormat.format(Date())

            // TODO: Cargar desde repository cuando esté implementado
            val mockTimeSlots = generateMockTimeSlots()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    selectedDate = currentDate,
                    timeSlots = mockTimeSlots
                )
            }
        }
    }

    private fun generateMockTimeSlots(): List<TimeSlotData> {
        return listOf(
            TimeSlotData("1:00 a.m.", null, 0f),
            TimeSlotData("2:00 a.m.", null, 0f),
            TimeSlotData(
                "3:00 a.m.",
                Task(
                    id = "1",
                    title = "Revisar código",
                    points = "100pts",
                    priority = "Media",
                    time = "3:00 a.m.",
                    isToday = true
                ),
                0.6f
            ),
            TimeSlotData(
                "4:00 a.m.",
                Task(
                    id = "2",
                    title = "Terminar los diseños de Figma",
                    points = "200pts",
                    priority = "Alta",
                    time = "4:00 a.m.",
                    isToday = true
                ),
                0f
            ),
            TimeSlotData("5:00 a.m.", null, 0f),
            TimeSlotData("6:00 a.m.", null, 0f),
            TimeSlotData("7:00 a.m.", null, 0f),
            TimeSlotData("8:00 a.m.", null, 0f)
        )
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
        // TODO: Abrir diálogo para crear tarea en este horario
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