package com.e243768.organipro_.presentation.viewmodels.tasks.monthly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.presentation.viewmodels.tasks.daily.TaskTab
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MonthlyTasksViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MonthlyTasksUiState())
    val uiState: StateFlow<MonthlyTasksUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    private val calendar = Calendar.getInstance(Locale("es", "ES"))

    init {
        loadMonthlyTasks()
    }

    fun onEvent(event: MonthlyTasksUiEvent) {
        when (event) {
            is MonthlyTasksUiEvent.TabSelected -> handleTabSelection(event.tab)
            is MonthlyTasksUiEvent.DayClicked -> handleDayClick(event.day)
            is MonthlyTasksUiEvent.BackClicked -> handleBackClick()
            is MonthlyTasksUiEvent.RefreshTasks -> loadMonthlyTasks()
            is MonthlyTasksUiEvent.PreviousMonth -> handlePreviousMonth()
            is MonthlyTasksUiEvent.NextMonth -> handleNextMonth()
        }
    }

    private fun loadMonthlyTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            delay(1000)

            // Obtener mes actual
            val monthFormat = SimpleDateFormat("MMMM", Locale("es", "ES"))
            val currentMonth = monthFormat.format(calendar.time).replaceFirstChar { it.uppercase() }

            // Días de la semana
            val daysOfWeek = listOf("Lun.", "Mar.", "Mié.", "Jue.", "Vie", "Sáb.", "Dom.")

            // Generar días del mes
            val monthDays = generateMonthDays()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentMonth = currentMonth,
                    daysOfWeek = daysOfWeek,
                    monthDays = monthDays
                )
            }
        }
    }

    private fun generateMonthDays(): List<MonthDayData> {
        val days = mutableListOf<MonthDayData>()

        // Clonar el calendario para no modificar el original
        val tempCalendar = calendar.clone() as Calendar

        // Ir al primer día del mes
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)

        // Obtener el día de la semana del primer día (1 = Domingo, 2 = Lunes, etc.)
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)

        // Ajustar para que Lunes sea el primer día (Calendar usa Domingo = 1)
        val daysToSkip = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2

        // Agregar días vacíos al inicio
        repeat(daysToSkip) {
            days.add(MonthDayData(dayNumber = 0)) // 0 indica día vacío
        }

        // Obtener el número de días en el mes
        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Día actual
        val today = Calendar.getInstance(Locale("es", "ES"))
        val isCurrentMonth = tempCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                tempCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
        val currentDay = if (isCurrentMonth) today.get(Calendar.DAY_OF_MONTH) else -1

        // Agregar todos los días del mes
        for (day in 1..daysInMonth) {
            // TODO: Cargar desde repository si el día tiene tareas
            val hasTask = (day == 15 || day == 20 || day == 25) // Mock data

            days.add(
                MonthDayData(
                    dayNumber = day,
                    hasTask = hasTask,
                    isCurrentDay = day == currentDay,
                    isSelected = day == _uiState.value.selectedDay,
                    taskCount = if (hasTask) 1 else 0
                )
            )
        }

        return days
    }

    private fun handleTabSelection(tab: TaskTab) {
        _uiState.update { it.copy(selectedTab = tab) }

        when (tab) {
            TaskTab.DAY -> _navigationEvent.value = NavigationEvent.NavigateToDaily
            TaskTab.WEEK -> _navigationEvent.value = NavigationEvent.NavigateToWeekly
            TaskTab.MONTH -> loadMonthlyTasks()
        }
    }

    private fun handleDayClick(day: Int) {
        _uiState.update {
            it.copy(
                selectedDay = day,
                monthDays = it.monthDays.map { monthDay ->
                    monthDay.copy(isSelected = monthDay.dayNumber == day)
                }
            )
        }

        // TODO: Navegar a vista de tareas del día seleccionado
        println("Day clicked: $day")
    }

    private fun handlePreviousMonth() {
        calendar.add(Calendar.MONTH, -1)
        loadMonthlyTasks()
    }

    private fun handleNextMonth() {
        calendar.add(Calendar.MONTH, 1)
        loadMonthlyTasks()
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
        object NavigateToWeekly : NavigationEvent()
    }
}