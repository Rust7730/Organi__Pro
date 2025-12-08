package com.e243768.organipro_.presentation.viewmodels.auth.signup

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
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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
                isNameValid = ValidationUtils.isValidName(name),
                error = null
            )
        }
    }

    private fun handleAliasChanged(alias: String) {
        _uiState.update {
            it.copy(
                alias = alias,
                isAliasValid = ValidationUtils.isValidAlias(alias),
                error = null
            )
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

    private fun handleSignUp() {
        val currentState = _uiState.value

        val isNameValid = ValidationUtils.isValidName(currentState.name)
        val isAliasValid = ValidationUtils.isValidAlias(currentState.alias)
        val isEmailValid = ValidationUtils.isValidEmail(currentState.email)
        val isPasswordValid = ValidationUtils.isValidPassword(currentState.password)

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

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.signUp(
                name = currentState.name,
                alias = currentState.alias,
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
                else -> {}
            }
        }
    }

    private fun handleLoginClick() {
        _navigationEvent.value = NavigationEvent.NavigateToLogin
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateToHome : NavigationEvent()
        object NavigateToLogin : NavigationEvent()
    }
}