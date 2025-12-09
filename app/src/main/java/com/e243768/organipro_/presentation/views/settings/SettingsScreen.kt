// presentation/views/settings/SettingsScreen.kt
package com.e243768.organipro_.presentation.views.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.e243768.organipro_.presentation.components.SharedBottomNavigation
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.viewmodels.settings.SettingsUiEvent
import com.e243768.organipro_.presentation.viewmodels.settings.SettingsViewModel
import com.e243768.organipro_.presentation.views.settings.components.SettingsHeader
import com.e243768.organipro_.presentation.views.settings.components.SettingsItem
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Manejar navegación
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is SettingsViewModel.NavigationEvent.NavigateBack -> {
                navController.popBackStack()
                viewModel.onNavigationHandled()
            }
            null -> { /* No hacer nada */ }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientWithStarsBackground()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                SharedBottomNavigation(
                    navController = navController,
                    currentRoute = Routes.Settings
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Header
                item {
                    SettingsHeader()
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Opciones de configuración
                items(uiState.settingsOptions) { option ->
                    SettingsItem(
                        option = option,
                        onClick = {
                            when (option.id) {
                                "language" -> viewModel.onEvent(SettingsUiEvent.LanguageClicked)
                                "theme" -> viewModel.onEvent(SettingsUiEvent.ThemeClicked)
                                "fonts" -> viewModel.onEvent(SettingsUiEvent.FontsClicked)
                                "accessibility" -> viewModel.onEvent(SettingsUiEvent.AccessibilityClicked)
                                "volume" -> viewModel.onEvent(SettingsUiEvent.VolumeClicked)
                                "cloud" -> viewModel.onEvent(SettingsUiEvent.CloudStorageClicked)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Espaciado final
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}