package com.e243768.organipro_.presentation.viewmodels.intro

data class IntroUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 3,
    val isLastPage: Boolean = false
)