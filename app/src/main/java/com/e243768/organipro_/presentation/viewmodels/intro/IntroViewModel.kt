package com.e243768.organipro_.presentation.viewmodels.intro

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class IntroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(IntroUiState())
    val uiState: StateFlow<IntroUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    fun onEvent(event: IntroUiEvent) {
        when (event) {
            is IntroUiEvent.NextPage -> nextPage()
            is IntroUiEvent.PreviousPage -> previousPage()
            is IntroUiEvent.SkipIntro -> skipIntro()
            is IntroUiEvent.FinishIntro -> finishIntro()
        }
    }

    private fun nextPage() {
        _uiState.update { currentState ->
            val newPage = (currentState.currentPage + 1).coerceAtMost(currentState.totalPages - 1)
            currentState.copy(
                currentPage = newPage,
                isLastPage = newPage == currentState.totalPages - 1
            )
        }
    }

    private fun previousPage() {
        _uiState.update { currentState ->
            val newPage = (currentState.currentPage - 1).coerceAtLeast(0)
            currentState.copy(
                currentPage = newPage,
                isLastPage = false
            )
        }
    }

    private fun skipIntro() {
        // TODO: Guardar en DataStore que ya vio el intro
        _navigationEvent.value = NavigationEvent.NavigateToLogin
    }

    private fun finishIntro() {
        // TODO: Guardar en DataStore que ya vio el intro
        _navigationEvent.value = NavigationEvent.NavigateToLogin
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateToLogin : NavigationEvent()
    }
}