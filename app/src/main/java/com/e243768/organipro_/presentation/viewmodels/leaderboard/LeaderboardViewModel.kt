package com.e243768.organipro_.presentation.viewmodels.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun onEvent(event: LeaderboardUiEvent) {
        when (event) {
            is LeaderboardUiEvent.TabSelected -> handleTabSelection(event.tab)
            is LeaderboardUiEvent.UserClicked -> handleUserClick(event.userId)
            is LeaderboardUiEvent.RefreshLeaderboard -> loadLeaderboard()
        }
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            delay(1000)

            // TODO: Cargar desde repository cuando esté implementado
            val mockData = generateMockLeaderboard()

            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentUser = mockData.first,
                    topThree = mockData.second.take(3),
                    otherUsers = mockData.second.drop(3)
                )
            }
        }
    }

    private fun generateMockLeaderboard(): Pair<LeaderboardUser, List<LeaderboardUser>> {
        val currentUser = LeaderboardUser(
            id = "current",
            name = "RustlessCar7730",
            rank = 10,
            points = "15,240 pts.",
            isCurrentUser = true
        )

        val topUsers = listOf(
            LeaderboardUser(
                id = "1",
                name = "Usuario",
                rank = 1,
                points = "25,500 pts."
            ),
            LeaderboardUser(
                id = "2",
                name = "Usuario",
                rank = 2,
                points = "23,100 pts."
            ),
            LeaderboardUser(
                id = "3",
                name = "Usuario",
                rank = 3,
                points = "21,800 pts."
            ),
            LeaderboardUser(
                id = "4",
                name = "Usuario",
                rank = 4,
                points = "19,500 pts."
            ),
            LeaderboardUser(
                id = "5",
                name = "Usuario",
                rank = 5,
                points = "18,200 pts."
            ),
            LeaderboardUser(
                id = "6",
                name = "Usuario",
                rank = 6,
                points = "17,100 pts."
            ),
            LeaderboardUser(
                id = "7",
                name = "Usuario",
                rank = 7,
                points = "16,500 pts."
            ),
            LeaderboardUser(
                id = "8",
                name = "Usuario",
                rank = 8,
                points = "15,800 pts."
            ),
            currentUser
        )

        return Pair(currentUser, topUsers)
    }

    private fun handleTabSelection(tab: LeaderboardTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        loadLeaderboard() // Recargar con nueva tab
    }

    private fun handleUserClick(userId: String) {
        // TODO: Navegar a perfil de usuario
        println("User clicked: $userId")
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        data class NavigateToUserProfile(val userId: String) : NavigationEvent()
    }
}