package com.e243768.organipro_.presentation.viewmodels.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.core.util.ValidationUtils
import com.e243768.organipro_.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, error = null) }
            }
            is LoginUiEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, error = null) }
            }
            is LoginUiEvent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
            }
            is LoginUiEvent.LoginClicked -> handleLogin()
            is LoginUiEvent.SignUpClicked -> _navigationEvent.value = NavigationEvent.NavigateToSignUp
            is LoginUiEvent.ForgotPasswordClicked -> handleForgotPassword()
        }
    }

    private fun handleLogin() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (!ValidationUtils.isValidEmail(email)) {
            _uiState.update { it.copy(error = "Email inválido", isEmailValid = false) }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu contraseña", isPasswordValid = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.signIn(email, password)

            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _navigationEvent.value = NavigationEvent.NavigateToHome
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                else -> {}
            }
        }
    }

    private fun handleForgotPassword() {
        val email = _uiState.value.email.trim()
        if (!ValidationUtils.isValidEmail(email)) {
            _uiState.update { it.copy(error = "Ingresa tu email para recuperar la contraseña") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.resetPassword(email)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = if (result is Result.Error) result.message else "Correo de recuperación enviado"
                )
            }
        }
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateToHome : NavigationEvent()
        object NavigateToSignUp : NavigationEvent()
    }
}