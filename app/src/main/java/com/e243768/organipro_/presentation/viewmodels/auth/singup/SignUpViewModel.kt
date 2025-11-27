package com.e243768.organipro_.presentation.viewmodels.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.NameChanged -> handleNameChanged(event.name)
            is SignUpUiEvent.AliasChanged -> handleAliasChanged(event.alias)
            is SignUpUiEvent.EmailChanged -> handleEmailChanged(event.email)
            is SignUpUiEvent.PasswordChanged -> handlePasswordChanged(event.password)
            is SignUpUiEvent.TogglePasswordVisibility -> togglePasswordVisibility()
            is SignUpUiEvent.SignUpClicked -> handleSignUp()
            is SignUpUiEvent.LoginClicked -> handleLoginClick()
        }
    }

    private fun handleNameChanged(name: String) {
        _uiState.update {
            it.copy(
                name = name,
                isNameValid = validateName(name),
                error = null
            )
        }
    }

    private fun handleAliasChanged(alias: String) {
        _uiState.update {
            it.copy(
                alias = alias,
                isAliasValid = validateAlias(alias),
                error = null
            )
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

    private fun handleSignUp() {
        val currentState = _uiState.value

        // Validar todos los campos
        val isNameValid = validateName(currentState.name)
        val isAliasValid = validateAlias(currentState.alias)
        val isEmailValid = validateEmail(currentState.email)
        val isPasswordValid = validatePassword(currentState.password)

        _uiState.update {
            it.copy(
                isNameValid = isNameValid,
                isAliasValid = isAliasValid,
                isEmailValid = isEmailValid,
                isPasswordValid = isPasswordValid
            )
        }

        if (!isNameValid || !isAliasValid || !isEmailValid || !isPasswordValid) {
            _uiState.update {
                it.copy(error = "Por favor, completa todos los campos correctamente")
            }
            return
        }

        // Simular registro
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            delay(2000) // Simular llamada a API

            // TODO: Implementar lógica real de registro con Firebase
            // Por ahora solo navegamos al Home
            _uiState.update { it.copy(isLoading = false) }
            _navigationEvent.value = NavigationEvent.NavigateToHome
        }
    }

    private fun handleLoginClick() {
        _navigationEvent.value = NavigationEvent.NavigateToLogin
    }

    private fun validateName(name: String): Boolean {
        if (name.isBlank()) return true // No mostrar error si está vacío
        return name.length >= 2
    }

    private fun validateAlias(alias: String): Boolean {
        if (alias.isBlank()) return true // No mostrar error si está vacío
        return alias.length >= 3
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
        object NavigateToLogin : NavigationEvent()
    }
}