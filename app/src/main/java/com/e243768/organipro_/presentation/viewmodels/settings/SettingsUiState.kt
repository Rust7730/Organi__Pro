package com.e243768.organipro_.presentation.viewmodels.settings

data class SettingsUiState(
    val settingsOptions: List<SettingOption> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class SettingOption(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val hasArrow: Boolean = true
)