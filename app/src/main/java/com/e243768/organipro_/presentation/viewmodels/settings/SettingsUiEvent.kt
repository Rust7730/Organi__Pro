package com.e243768.organipro_.presentation.viewmodels.settings

sealed class SettingsUiEvent {
    object BackClicked : SettingsUiEvent()
    object LanguageClicked : SettingsUiEvent()
    object ThemeClicked : SettingsUiEvent()
    object FontsClicked : SettingsUiEvent()
    object AccessibilityClicked : SettingsUiEvent()
    object VolumeClicked : SettingsUiEvent()
    object CloudStorageClicked : SettingsUiEvent()
}