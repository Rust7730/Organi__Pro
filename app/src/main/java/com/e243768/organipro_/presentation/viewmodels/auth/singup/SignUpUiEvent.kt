package com.e243768.organipro_.presentation.viewmodels.auth.signup

sealed class SignUpUiEvent {
    data class NameChanged(val name: String) : SignUpUiEvent()
    data class AliasChanged(val alias: String) : SignUpUiEvent()
    data class EmailChanged(val email: String) : SignUpUiEvent()
    data class PasswordChanged(val password: String) : SignUpUiEvent()
    object TogglePasswordVisibility : SignUpUiEvent()
    object SignUpClicked : SignUpUiEvent()
    object LoginClicked : SignUpUiEvent()
}