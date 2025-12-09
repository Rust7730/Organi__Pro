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
    private var cachedTasks: List<Task> = emptyList() // <--- AGREGADO: Cache para re-mapear al seleccionar día

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

            _uiState.update {
                it.copy(
                    isLoading = true,
                    monthLabel = DateUtils.formatMonthYear(currentCalendar.time)
                )
            }

            taskRepository.getMonthTasks(userId).collect { tasks ->
                cachedTasks = tasks // Guardamos las tareas
                updateGrid() // Actualizamos la UI
            }
        }
    }

    // Función auxiliar para actualizar la UI (se llama al cargar datos o al cambiar selección)
    private fun updateGrid() {
        val selectedDate = _uiState.value.selectedDate
        val gridData = mapTasksToMonthGrid(cachedTasks, currentCalendar, selectedDate)

        _uiState.update {
            it.copy(
                isLoading = false,
                days = gridData
            )
        }
    }

    private fun mapTasksToMonthGrid(tasks: List<Task>, calendar: Calendar, selectedDate: Date): List<MonthlyDayData> {
        val days = mutableListOf<MonthlyDayData>()
        val tempCal = calendar.clone() as Calendar

        tempCal.set(Calendar.DAY_OF_MONTH, 1)
        val maxDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)

        val startOffset = if (dayOfWeek == Calendar.SUNDAY) 6 else dayOfWeek - 2

        for (i in 0 until startOffset) {
            days.add(MonthlyDayData(date = null))
        }

        for (i in 1..maxDays) {
            val date = tempCal.time
            val isToday = DateUtils.isToday(date)
            // Verificar si es el día seleccionado
            val isSelected = DateUtils.isSameDay(date, selectedDate)

            val tasksForDay = tasks.filter { DateUtils.isSameDay(it.dueDate, date) }
            val completed = tasksForDay.count { it.isCompleted() }

            days.add(
                MonthlyDayData(
                    date = date,
                    dayNumber = i.toString(),
                    hasTasks = tasksForDay.isNotEmpty(),
                    completedCount = completed,
                    totalCount = tasksForDay.size,
                    tasks = tasksForDay, // <--- AGREGADO: Pasamos las tareas
                    isToday = isToday,
                    isSelected = isSelected // <--- AGREGADO: Marcamos selección
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
        // Actualizamos la fecha seleccionada y refrescamos la grilla
        _uiState.update { it.copy(selectedDate = date) }
        updateGrid()
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
    }
}