package com.e243768.organipro_.presentation.viewmodels.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: LeaderboardRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    private var leaderboardJob: Job? = null

    init {
        // Al iniciar, cargamos datos locales y pedimos actualización remota
        refreshLeaderboard()
        observeLeaderboard()
    }

    fun onEvent(event: LeaderboardUiEvent) {
        when (event) {
            is LeaderboardUiEvent.TabSelected -> handleTabSelection(event.tab)
            is LeaderboardUiEvent.UserClicked -> handleUserClick(event.userId)
            is LeaderboardUiEvent.RefreshLeaderboard -> refreshLeaderboard()
        }
    }

    private fun observeLeaderboard() {
        // Cancelamos observación anterior si cambiamos de tab
        leaderboardJob?.cancel()

        leaderboardJob = viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId() ?: ""
            val selectedTab = _uiState.value.selectedTab

            // Seleccionamos el flujo según el tab (Semanal o Mensual)
            val flow = if (selectedTab == LeaderboardTab.WEEKLY) {
                leaderboardRepository.getWeeklyTopRankings()
            } else {
                leaderboardRepository.getMonthlyTopRankings()
            }

            flow.collect { rankings ->
                // Si no hay rankings locales, mostrar mensaje para ayudar a depuración
                if (rankings.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, error = "No hay rankings disponibles. Asegúrate de que hay datos en Firestore y sincroniza.") }
                    return@collect
                } else {
                    // limpiar error previo
                    _uiState.update { it.copy(error = null) }
                }
                val uiUsers = rankings.map { rank ->
                    LeaderboardUser(
                        id = rank.userId,
                        name = rank.getDisplayName(),
                        rank = rank.rank,
                        points = if (selectedTab == LeaderboardTab.WEEKLY)
                            "${rank.weeklyPoints} pts"
                        else
                            "${rank.monthlyPoints} pts",
                        avatarUrl = rank.avatarUrl,
                        level = rank.level,
                        streak = rank.streak,
                        avatarResId = 0, // Placeholder if no avatarUrl
                        isCurrentUser = rank.userId == currentUserId
                    )
                }

                val currentUser = uiUsers.find { it.isCurrentUser }
                val topThree = uiUsers.take(3)
                val others = uiUsers.drop(3)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = currentUser,
                        topThree = topThree,
                        otherUsers = others
                    )
                }
            }
        }
    }

    private fun refreshLeaderboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            // Intentamos obtener directamente los rankings remotos para diagnosticar si vienen vacíos
            when (val res = leaderboardRepository.fetchLeaderboardFromRemote()) {
                is Result.Success -> {
                    val list = res.data
                    if (list.isEmpty()) {
                        _uiState.update { it.copy(isLoading = false, error = "Sin rankings remotos (fetched=0). Revisa collection 'user_stats' o 'users' en Firestore.") }
                    } else {
                        // Actualizará la DB local y el flow observador refrescará la UI
                        _uiState.update { it.copy(isLoading = false, error = null) }
                    }
                }
                is Result.Error -> {
                    val msg = res.message ?: "Error sincronizando leaderboard"
                    _uiState.update { it.copy(isLoading = false, error = msg) }
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleTabSelection(tab: LeaderboardTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        observeLeaderboard() // Re-observar con el nuevo filtro
    }

    private fun handleUserClick(userId: String) {
        // TODO: Navegar al perfil público del usuario
        println("Click en usuario: $userId")
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        data class NavigateToUserProfile(val userId: String) : NavigationEvent()
    }
}