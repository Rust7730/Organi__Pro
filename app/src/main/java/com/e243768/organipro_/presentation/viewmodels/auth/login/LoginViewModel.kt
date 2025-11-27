package com.e243768.organipro_.presentation.viewmodels.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> handleEmailChanged(event.email)
            is LoginUiEvent.PasswordChanged -> handlePasswordChanged(event.password)
            is LoginUiEvent.TogglePasswordVisibility -> togglePasswordVisibility()
            is LoginUiEvent.LoginClicked -> handleLogin()
            is LoginUiEvent.SignUpClicked -> handleSignUpClick()
            is LoginUiEvent.ForgotPasswordClicked -> handleForgotPassword()
        }
    }

    private fun handleEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                isEmailValid = validateEmail(email),
                error = null
            )
        }
    }

    private fun handlePasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                isPasswordValid = validatePassword(password),
                error = null
            )
        }
    }

    private fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(passwordVisible = !it.passwordVisible)
        }
    }

    private fun handleLogin() {
        val currentState = _uiState.value

        // Validar campos
        val isEmailValid = validateEmail(currentState.email)
        val isPasswordValid = validatePassword(currentState.password)

        _uiState.update {
            it.copy(
                isEmailValid = isEmailValid,
                isPasswordValid = isPasswordValid
            )
        }

        if (!isEmailValid || !isPasswordValid) {
            _uiState.update {
                it.copy(error = "Por favor, verifica los campos")
            }
            return
        }

        // Simular login
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            delay(2000) // Simular llamada a API

            _uiState.update { it.copy(isLoading = false) }
            _navigationEvent.value = NavigationEvent.NavigateToHome
        }
    }

    private fun handleSignUpClick() {
        _navigationEvent.value = NavigationEvent.NavigateToSignUp
    }

    private fun handleForgotPassword() {
        // TODO: Implementar recuperación de contraseña
        _uiState.update {
            it.copy(error = "Funcionalidad próximamente disponible")
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isBlank()) return true // No mostrar error si está vacío
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isBlank()) return true // No mostrar error si está vacío
        return password.length >= 6
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateToHome : NavigationEvent()
        object NavigateToSignUp : NavigationEvent()
    }
}