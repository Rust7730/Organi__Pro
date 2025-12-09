// presentation/views/profile/ProfileScreen.kt
package com.e243768.organipro_.presentation.views.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.e243768.organipro_.presentation.components.SharedBottomNavigation
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.viewmodels.profile.ProfileUiEvent
import com.e243768.organipro_.presentation.viewmodels.profile.ProfileViewModel
import com.e243768.organipro_.presentation.views.profile.components.*
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.onEvent(ProfileUiEvent.PhotoSelected(uri))
            }
        }
    )
    // Manejar navegación
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is ProfileViewModel.NavigationEvent.NavigateToSettings -> {
                navController.navigate(Routes.Settings)
                viewModel.onNavigationHandled()
            }
            null -> { /* No hacer nada */ }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientWithStarsBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ProfileHeader(
                    userName = uiState.userName,
                    onSettingsClick = {
                        viewModel.onEvent(ProfileUiEvent.SettingsClicked)
                    }
                )
            },
            bottomBar = {
                SharedBottomNavigation(
                    navController = navController,
                    currentRoute = Routes.Profile
                )
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(vertical = 32.dp)
                ) {
                    // Avatar
                    item {
                        ProfileAvatar(
                            imageUrl = uiState.photoUrl,
                            onEditClick = {
                                // Lanzar el selector de fotos
                                singlePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Nivel y progreso
                    item {
                        LevelProgress(
                            currentLevel = uiState.currentLevel,
                            currentXP = uiState.currentXP,
                            maxXP = uiState.maxXP
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    // Estadísticas
                    item {
                        ProfileStats(
                            totalPoints = uiState.totalPoints,
                            tasksCompleted = uiState.tasksCompleted,
                            currentStreak = uiState.currentStreak
                        )
                    }
                }
            }
        }
    }
}