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
                isEmailValid = ValidationUtils.isValidEmail(email),
                error = null
            )
        }
    }

    private fun handlePasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                isPasswordValid = ValidationUtils.isValidPassword(password),
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

        val isEmailValid = ValidationUtils.isValidEmail(currentState.email)
        val isPasswordValid = ValidationUtils.isValidPassword(currentState.password)

        _uiState.update {
            it.copy(
                isEmailValid = isEmailValid,
                isPasswordValid = isPasswordValid
            )
        }

        if (!isEmailValid || !isPasswordValid) {
            _uiState.update {
                it.copy(error = "Por favor, verifica tus credenciales")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.signIn(
                email = currentState.email,
                password = currentState.password
            )

            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _navigationEvent.value = NavigationEvent.NavigateToHome
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                else -> { /* Manejar Loading si fuera necesario */ }
            }
        }
    }

    private fun handleSignUpClick() {
        _navigationEvent.value = NavigationEvent.NavigateToSignUp
    }

    private fun handleForgotPassword() {
        val email = _uiState.value.email
        if (!ValidationUtils.isValidEmail(email)) {
            _uiState.update { it.copy(error = "Ingresa tu email para recuperar la contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = authRepository.resetPassword(email)
            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(error = "Se ha enviado un correo de recuperación") }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                else -> {}
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