package com.e243768.organipro_.presentation.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.UserRepository
import com.e243768.organipro_.domain.repository.UserStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.net.Uri
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userStatsRepository: UserStatsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.SettingsClicked -> handleSettingsClick()
            is ProfileUiEvent.AvatarClicked -> handleAvatarClick()
            is ProfileUiEvent.RefreshProfile -> loadProfile()
            is ProfileUiEvent.PhotoSelected -> uploadProfilePhoto(event.uri)
        }
    }
// En ProfileViewModel.kt

    private fun uploadProfilePhoto(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Sesión no válida") }
                return@launch
            }

            // Llama a la función que ya creaste en el repositorio
            val result = userRepository.updateProfilePhoto(userId, uri)

            when (result) {
                is Result.Success -> {
                    val newUrl = result.data
                    // Actualiza el estado de la UI inmediatamente con la nueva URL
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            photoUrl = newUrl,
                            // Limpia cualquier error previo
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Error al subir imagen"
                        )
                    }
                }

                Result.Loading -> TODO()
            }
        }
    }    private fun loadProfile() {
        viewModelScope.launch {
            // 1. Obtener ID del usuario actual
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Sesión no válida") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            // 2. Cargar datos básicos del Usuario (Nombre, Nivel, XP)
            val userResult = userRepository.getUserById(userId)
            if (userResult is Result.Success) {
                val user = userResult.data
                _uiState.update {
                    it.copy(
                        userName = user.getDisplayName(),
                        userAlias = user.alias, // Asegúrate de que tu modelo User tenga 'alias'
                        currentLevel = user.level,
                        currentXP = user.currentXP,
                        maxXP = user.getXPForNextLevel(), // Método del dominio
                        avatarResId = 0 // TODO: Manejar URL de avatar real
                    )
                }
            }

            // 3. Cargar Estadísticas detalladas (Puntos, Tareas, Rachas)
            // Usamos UserStatsRepository para obtener datos agregados
            val statsResult = userStatsRepository.getUserStats(userId)
            if (statsResult is Result.Success) {
                val stats = statsResult.data
                _uiState.update {
                    it.copy(
                        totalPoints = stats.totalPoints,
                        tasksCompleted = stats.tasksCompleted,
                        currentStreak = stats.currentStreak
                    )
                }
            } else {
                // Si falla stats, intentamos usar los datos del usuario como fallback
                if (userResult is Result.Success) {
                    val user = userResult.data
                    _uiState.update {
                        it.copy(
                            totalPoints = user.totalPoints,
                            tasksCompleted = user.tasksCompleted,
                            currentStreak = user.currentStreak
                        )
                    }
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun handleSettingsClick() {
        _navigationEvent.value = NavigationEvent.NavigateToSettings
    }

    private fun handleAvatarClick() {
        // TODO: Implementar lógica de selección de imagen/avatar
        println("Avatar clicked")
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateToSettings : NavigationEvent()
    }
}