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
            is SignUpUiEvent.NameChanged -> _uiState.update { it.copy(name = event.name, error = null) }
            is SignUpUiEvent.AliasChanged -> _uiState.update { it.copy(alias = event.alias, error = null) }
            is SignUpUiEvent.EmailChanged -> _uiState.update { it.copy(email = event.email, error = null) }
            is SignUpUiEvent.PasswordChanged -> _uiState.update { it.copy(password = event.password, error = null) }
            is SignUpUiEvent.TogglePasswordVisibility -> _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
            is SignUpUiEvent.SignUpClicked -> handleSignUp()
            is SignUpUiEvent.LoginClicked -> _navigationEvent.value = NavigationEvent.NavigateToLogin
        }
    }

    private fun handleSignUp() {
        val name = _uiState.value.name.trim()
        val alias = _uiState.value.alias.trim()
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (!ValidationUtils.isValidName(name)) {
            _uiState.update { it.copy(error = "Nombre inválido (mínimo 2 letras)", isNameValid = false) }
            return
        }
        if (!ValidationUtils.isValidAlias(alias)) {
            _uiState.update { it.copy(error = "Alias inválido (mínimo 3 letras)", isAliasValid = false) }
            return
        }
        if (!ValidationUtils.isValidEmail(email)) {
            _uiState.update { it.copy(error = "Email inválido", isEmailValid = false) }
            return
        }
        if (!ValidationUtils.isValidPassword(password)) {
            _uiState.update { it.copy(error = "Contraseña muy corta (mínimo 6 caracteres)", isPasswordValid = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.signUp(name, alias, email, password)

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

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateToHome : NavigationEvent()
        object NavigateToLogin : NavigationEvent()
    }
}