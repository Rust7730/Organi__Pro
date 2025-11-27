package com.e243768.organipro_.presentation.viewmodels.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkInitialState()
    }

    private fun checkInitialState() {
        viewModelScope.launch {
            // Simular carga inicial de la app
            delay(2000)

            // Por ahora siempre navegamos a Intro
            // TODO: En el futuro verificar:
            // - Si es primera vez → Intro
            // - Si hay sesión activa → Home
            // - Si ya vio el intro → Login
            _uiState.update {
                it.copy(
                    isLoading = false,
                    navigateToIntro = true
                )
            }
        }
    }
}