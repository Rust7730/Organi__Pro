package com.e243768.organipro_.presentation.viewmodels.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkInitialState()
    }

    private fun checkInitialState() {
        viewModelScope.launch {
            // Mínimo tiempo de splash para mostrar la marca (2s)
            val minimumDelay = launch { delay(2000) }

            // Verificar sesión real en paralelo
            // TODO: Aquí también podríamos precargar datos esenciales si fuera necesario
            val isUserLoggedIn = authRepository.isUserLoggedIn()

            // Esperar a que termine el delay visual
            minimumDelay.join()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    navigateToHome = isUserLoggedIn,
                    navigateToIntro = !isUserLoggedIn
                )
            }
        }
    }
}