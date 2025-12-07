package com.e243768.organipro_.presentation.viewmodels.tasks.weekly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.domain.model.Task
import com.e243768.organipro_.presentation.viewmodels.tasks.daily.TaskTab
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WeeklyTasksViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyTasksUiState())
    val uiState: StateFlow<WeeklyTasksUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        loadWeeklyTasks()
    }

    fun onEvent(event: WeeklyTasksUiEvent) {
        when (event) {
            is WeeklyTasksUiEvent.TabSelected -> handleTabSelection(event.tab)
            is WeeklyTasksUiEvent.TaskClicked -> handleTaskClick(event.task)
            is WeeklyTasksUiEvent.DaySlotClicked -> handleDaySlotClick(event.day, event.time)
            is WeeklyTasksUiEvent.BackClicked -> handleBackClick()
            is WeeklyTasksUiEvent.RefreshTasks -> loadWeeklyTasks()
        }
    }

    private fun loadWeeklyTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            delay(1000)

            // Obtener rango de la semana
            val calendar = Calendar.getInstance(Locale("es", "ES"))
            calendar.firstDayOfWeek = Calendar.MONDAY
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

            val startDate = calendar.time
            calendar.add(Calendar.DAY_OF_WEEK, 6)
            val endDate = calendar.time

            val dateFormat = SimpleDateFormat("d 'de' MMMM", Locale("es", "ES"))
            val weekRange = "Lun. ${dateFormat.format(startDate)} - Dom. ${dateFormat.format(endDate)}"

            // Días de la semana
            val daysOfWeek = listOf("Lun.", "Mar.", "Mié.", "Jue.", "Vie", "Sáb.", "Dom.")

            // Horarios
            val timeSlots = listOf(
                "1:00 a.m.",
                "2:00 a.m.",
                "3:00 a.m.",
                "4:00 a.m.",
                "5:00 a.m.",
                "6:00 a.m."
            )

            // TODO: Cargar desde repository cuando esté implementado
            val mockWeeklyTasks = generateMockWeeklyTasks()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    weekRange = weekRange,
                    daysOfWeek = daysOfWeek,
                    timeSlots = timeSlots,
                    weeklyTasks = mockWeeklyTasks
                )
            }
        }
    }

    private fun generateMockWeeklyTasks(): Map<String, Map<String, WeeklyTaskData>> {
        // Crear algunas tareas de ejemplo
        val tasks = mutableMapOf<String, MutableMap<String, WeeklyTaskData>>()

        // Tarea en Lun. a las 3:00 a.m.
        tasks["Lun."] = mutableMapOf(
            "3:00 a.m." to WeeklyTaskData(
                task = Task(
                    id = "1",
                    title = "Revisar código",
                    points = "100pts",
                    priority = "Media",
                    time = "3:00 a.m.",
                    isToday = false
                ),
                progress = 0.6f
            )
        )

        // Tarea en Mar. a las 2:00 a.m.
        tasks["Mar."] = mutableMapOf(
            "2:00 a.m." to WeeklyTaskData(
                task = Task(
                    id = "2",
                    title = "Diseño UI",
                    points = "150pts",
                    priority = "Alta",
                    time = "2:00 a.m.",
                    isToday = false
                ),
                progress = 0.3f
            )
        )

        return tasks
    }

    private fun handleTabSelection(tab: TaskTab) {
        _uiState.update { it.copy(selectedTab = tab) }

        when (tab) {
            TaskTab.DAY -> _navigationEvent.value = NavigationEvent.NavigateToDaily
            TaskTab.WEEK -> loadWeeklyTasks()
            TaskTab.MONTH -> _navigationEvent.value = NavigationEvent.NavigateToMonthly
        }
    }

    private fun handleTaskClick(task: Task) {
        _navigationEvent.value = NavigationEvent.NavigateToTaskDetail(task.id)
    }

    private fun handleDaySlotClick(day: String, time: String) {
        // TODO: Abrir diálogo para crear tarea en este día/horario
        println("Day slot clicked: $day at $time")
    }

    private fun handleBackClick() {
        _navigationEvent.value = NavigationEvent.NavigateBack
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
        object NavigateToDaily : NavigationEvent()
        object NavigateToMonthly : NavigationEvent()
        data class NavigateToTaskDetail(val taskId: String) : NavigationEvent()
    }
}