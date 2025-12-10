package com.e243768.organipro_.presentation.viewmodels.profile

data class ProfileUiState(
    val userName: String = "",
    val userAlias: String = "",
    val photoUrl: String? = null, // <--- AGREGA ESTO
    val currentLevel: Int = 0,
    val currentXP: Int = 0,
    val maxXP: Int = 100,
    val totalPoints: Int = 0,
    val tasksCompleted: Int = 0,
    val currentStreak: Int = 0,
    val avatarResId: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)