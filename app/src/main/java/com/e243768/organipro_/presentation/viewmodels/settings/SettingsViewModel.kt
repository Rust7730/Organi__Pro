package com.e243768.organipro_.presentation.viewmodels.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.BackClicked -> handleBackClick()
            is SettingsUiEvent.LanguageClicked -> handleLanguageClick()
            is SettingsUiEvent.ThemeClicked -> handleThemeClick()
            is SettingsUiEvent.FontsClicked -> handleFontsClick()
            is SettingsUiEvent.AccessibilityClicked -> handleAccessibilityClick()
            is SettingsUiEvent.VolumeClicked -> handleVolumeClick()
            is SettingsUiEvent.CloudStorageClicked -> handleCloudStorageClick()
        }
    }

    private fun loadSettings() {
        val settingsOptions = listOf(
            SettingOption(
                id = "language",
                title = "Idioma",
                subtitle = null
            ),
            SettingOption(
                id = "theme",
                title = "Temas",
                subtitle = null
            ),
            SettingOption(
                id = "fonts",
                title = "Fuentes",
                subtitle = null
            ),
            SettingOption(
                id = "accessibility",
                title = "Accesibilidad",
                subtitle = null
            ),
            SettingOption(
                id = "volume",
                title = "Volumen",
                subtitle = null
            ),
            SettingOption(
                id = "cloud",
                title = "Guardado en la nube",
                subtitle = null
            )
        )

        _uiState.update {
            it.copy(settingsOptions = settingsOptions)
        }
    }

    private fun handleBackClick() {
        _navigationEvent.value = NavigationEvent.NavigateBack
    }

    private fun handleLanguageClick() {
        // TODO: Navegar a pantalla de selección de idioma
        println("Language settings clicked")
    }

    private fun handleThemeClick() {
        // TODO: Navegar a pantalla de selección de tema
        println("Theme settings clicked")
    }

    private fun handleFontsClick() {
        // TODO: Navegar a pantalla de selección de fuentes
        println("Fonts settings clicked")
    }

    private fun handleAccessibilityClick() {
        // TODO: Navegar a pantalla de accesibilidad
        println("Accessibility settings clicked")
    }

    private fun handleVolumeClick() {
        // TODO: Navegar a pantalla de volumen
        println("Volume settings clicked")
    }

    private fun handleCloudStorageClick() {
        // TODO: Navegar a pantalla de almacenamiento en la nube
        println("Cloud storage settings clicked")
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
    }
}