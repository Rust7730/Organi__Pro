package com.e243768.organipro_.presentation.viewmodels.auth.signup

data class SignUpUiState(
    val name: String = "",
    val alias: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isNameValid: Boolean = true,
    val isAliasValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val passwordVisible: Boolean = false
)