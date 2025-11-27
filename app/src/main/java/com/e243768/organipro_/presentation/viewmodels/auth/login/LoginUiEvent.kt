package com.e243768.organipro_.presentation.viewmodels.auth.login

sealed class LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent()
    data class PasswordChanged(val password: String) : LoginUiEvent()
    object TogglePasswordVisibility : LoginUiEvent()
    object LoginClicked : LoginUiEvent()
    object SignUpClicked : LoginUiEvent()
    object ForgotPasswordClicked : LoginUiEvent()
}