package com.e243768.organipro_.presentation.viewmodels.leaderboard

data class LeaderboardUiState(
    val selectedTab: LeaderboardTab = LeaderboardTab.WEEKLY,
    val currentUser: LeaderboardUser? = null,
    val topThree: List<LeaderboardUser> = emptyList(),
    val otherUsers: List<LeaderboardUser> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class LeaderboardUser(
    val id: String,
    val name: String,
    val rank: Int,
    val points: String,
    val avatarResId: Int = 0,
    val isCurrentUser: Boolean = false
)