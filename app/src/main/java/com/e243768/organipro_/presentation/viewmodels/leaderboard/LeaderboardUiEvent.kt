package com.e243768.organipro_.presentation.viewmodels.leaderboard

sealed class LeaderboardUiEvent {
    data class TabSelected(val tab: LeaderboardTab) : LeaderboardUiEvent()
    data class UserClicked(val userId: String) : LeaderboardUiEvent()
    object RefreshLeaderboard : LeaderboardUiEvent()
}

enum class LeaderboardTab {
    WEEKLY, MONTHLY
}