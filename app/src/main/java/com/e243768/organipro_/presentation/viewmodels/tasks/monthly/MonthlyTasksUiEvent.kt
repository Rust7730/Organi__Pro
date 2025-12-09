package com.e243768.organipro_.presentation.viewmodels.tasks.monthly

import java.util.Date

sealed class MonthlyTasksUiEvent {
    data class DayClicked(val date: Date) : MonthlyTasksUiEvent()
    object PreviousMonthClicked : MonthlyTasksUiEvent()
    object NextMonthClicked : MonthlyTasksUiEvent()
    object BackClicked : MonthlyTasksUiEvent()
}