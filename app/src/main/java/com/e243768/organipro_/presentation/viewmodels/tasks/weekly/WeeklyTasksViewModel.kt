package com.e243768.organipro_.presentation.viewmodels.tasks.weekly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.core.util.DateUtils
import com.e243768.organipro_.domain.model.Task
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WeeklyTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyTasksUiState())
    val uiState: StateFlow<WeeklyTasksUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    private var currentCalendar = Calendar.getInstance()

    init {
        loadWeeklyTasks()
    }

    fun onEvent(event: WeeklyTasksUiEvent) {
        when (event) {
            is WeeklyTasksUiEvent.TaskClicked -> _navigationEvent.value = NavigationEvent.NavigateToTaskDetail(event.task.id)
            is WeeklyTasksUiEvent.DayClicked -> handleDayClick(event.date)
            is WeeklyTasksUiEvent.PreviousWeekClicked -> changeWeek(-1)
            is WeeklyTasksUiEvent.NextWeekClicked -> changeWeek(1)
            is WeeklyTasksUiEvent.BackClicked -> _navigationEvent.value = NavigationEvent.NavigateBack
            is WeeklyTasksUiEvent.CreateTaskClicked -> _navigationEvent.value = NavigationEvent.NavigateToCreateTask
        }
    }

    private fun loadWeeklyTasks() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            _uiState.update {
                it.copy(
                    isLoading = true,
                    weekLabel = DateUtils.formatWeekLabel(currentCalendar.time) // Ej: "Sem 42 - Octubre"
                )
            }

            // Usamos el repositorio para traer las tareas de esta semana
            // Nota: Aquí asumimos que getWeekTasks filtra por la semana actual del sistema.
            // Si queremos paginación (semanas pasadas), deberíamos usar getTasksByDateRange
            // Por simplicidad usaremos getWeekTasks, pero idealmente pasaríamos rangos.

            taskRepository.getWeekTasks(userId).collect { tasks ->
                val daysData = mapTasksToWeekDays(tasks, currentCalendar)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        days = daysData
                    )
                }
            }
        }
    }

    private fun mapTasksToWeekDays(tasks: List<Task>, calendar: Calendar): List<WeeklyDayData> {
        val days = mutableListOf<WeeklyDayData>()
        val tempCal = calendar.clone() as Calendar

        // Ajustar al lunes de la semana actual
        tempCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        if (tempCal.firstDayOfWeek == Calendar.SUNDAY && tempCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            // Ajustes menores de localización según sea necesario
        }

        for (i in 0..6) {
            val date = tempCal.time
            val isToday = DateUtils.isToday(date)

            // Filtrar tareas para este día específico
            val tasksForDay = tasks.filter { DateUtils.isSameDay(it.dueDate, date) }

            // Calcular progreso
            val completed = tasksForDay.count { it.isCompleted() }
            val progress = if (tasksForDay.isNotEmpty()) completed.toFloat() / tasksForDay.size else 0f

            days.add(
                WeeklyDayData(
                    date = date,
                    dayName = DateUtils.formatDayNameShort(date), // "Lun"
                    dayNumber = DateUtils.formatDayNumber(date), // "12"
                    tasks = tasksForDay,
                    isToday = isToday,
                    progress = progress
                )
            )
            tempCal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }

    private fun changeWeek(amount: Int) {
        currentCalendar.add(Calendar.WEEK_OF_YEAR, amount)
        // Recargar datos para la nueva semana (Nota: si getWeekTasks solo trae "esta" semana,
        // necesitaríamos cambiar el método del repo a getTasksByDateRange(start, end))
        loadWeeklyTasks()
    }

    private fun handleDayClick(date: Date) {
        _uiState.update {
            it.copy(selectedDate = date)
        }
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
        object NavigateToCreateTask : NavigationEvent()
        data class NavigateToTaskDetail(val taskId: String) : NavigationEvent()
    }
}