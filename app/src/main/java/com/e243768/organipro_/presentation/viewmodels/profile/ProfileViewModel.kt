package com.e243768.organipro_.presentation.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.SettingsClicked -> handleSettingsClick()
            is ProfileUiEvent.AvatarClicked -> handleAvatarClick()
            is ProfileUiEvent.RefreshProfile -> loadProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            delay(1000)

            // TODO: Cargar desde repository cuando esté implementado
            val mockProfile = ProfileUiState(
                userName = "RustlessCar7730",
                userAlias = "@rustless",
                currentLevel = 20,
                currentXP = 450,
                maxXP = 1000,
                totalPoints = 15240,
                tasksCompleted = 127,
                currentStreak = 20,
                avatarResId = 0,
                isLoading = false
            )

            _uiState.value = mockProfile
        }
    }

    private fun handleSettingsClick() {
        _navigationEvent.value = NavigationEvent.NavigateToSettings
    }

    private fun handleAvatarClick() {
        // TODO: Abrir selector de avatar
        println("Avatar clicked")
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateToSettings : NavigationEvent()
    }
}