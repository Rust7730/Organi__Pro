package com.e243768.organipro_.presentation.viewmodels.tasks.monthly

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
class MonthlyTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyTasksUiState())
    val uiState: StateFlow<MonthlyTasksUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    private var currentCalendar = Calendar.getInstance()

    init {
        loadMonthlyTasks()
    }

    fun onEvent(event: MonthlyTasksUiEvent) {
        when (event) {
            is MonthlyTasksUiEvent.DayClicked -> handleDayClick(event.date)
            is MonthlyTasksUiEvent.PreviousMonthClicked -> changeMonth(-1)
            is MonthlyTasksUiEvent.NextMonthClicked -> changeMonth(1)
            is MonthlyTasksUiEvent.BackClicked -> _navigationEvent.value = NavigationEvent.NavigateBack
        }
    }

    private fun loadMonthlyTasks() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch

            // Actualizar etiqueta del mes
            _uiState.update {
                it.copy(
                    isLoading = true,
                    monthLabel = DateUtils.formatMonthYear(currentCalendar.time)
                )
            }

            // Usamos getMonthTasks. De nuevo, asumimos que devuelve las tareas del mes "actual"
            // o implementamos lógica de fechas en el repo.
            taskRepository.getMonthTasks(userId).collect { tasks ->
                val gridData = mapTasksToMonthGrid(tasks, currentCalendar)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        days = gridData
                    )
                }
            }
        }
    }

    private fun mapTasksToMonthGrid(tasks: List<Task>, calendar: Calendar): List<MonthlyDayData> {
        val days = mutableListOf<MonthlyDayData>()
        val tempCal = calendar.clone() as Calendar

        // Ir al primer día del mes
        tempCal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) // 1=Domingo, 2=Lunes...

        // Ajustar padding inicial (si el mes empieza en Miércoles, rellenar Lun y Mar)
        // Suponiendo que la semana empieza en Lunes (Calendar.MONDAY = 2)
        val startOffset = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2

        // Agregar días vacíos al inicio
        for (i in 0 until startOffset) {
            days.add(MonthlyDayData(date = null))
        }

        // Agregar días del mes
        for (i in 1..maxDays) {
            val date = tempCal.time
            val isToday = DateUtils.isToday(date)

            val tasksForDay = tasks.filter { DateUtils.isSameDay(it.dueDate, date) }
            val completed = tasksForDay.count { it.isCompleted() }

            days.add(
                MonthlyDayData(
                    date = date,
                    dayNumber = i.toString(),
                    hasTasks = tasksForDay.isNotEmpty(),
                    completedCount = completed,
                    totalCount = tasksForDay.size,
                    isToday = isToday
                )
            )
            tempCal.add(Calendar.DAY_OF_MONTH, 1)
        }

        return days
    }

    private fun changeMonth(amount: Int) {
        currentCalendar.add(Calendar.MONTH, amount)
        loadMonthlyTasks()
    }

    private fun handleDayClick(date: Date) {
        // Navegar al día específico
        println("Date clicked: $date")
        // _navigationEvent.value = NavigationEvent.NavigateToDaily(date)
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
    }
}