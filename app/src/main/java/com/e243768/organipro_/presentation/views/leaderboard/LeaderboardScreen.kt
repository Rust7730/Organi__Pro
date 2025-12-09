// presentation/views/leaderboard/LeaderboardScreen.kt
package com.e243768.organipro_.presentation.views.leaderboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.e243768.organipro_.presentation.components.SharedBottomNavigation
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.viewmodels.leaderboard.LeaderboardUiEvent
import com.e243768.organipro_.presentation.viewmodels.leaderboard.LeaderboardViewModel
import com.e243768.organipro_.presentation.views.leaderboard.components.*
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground

@Composable
fun LeaderboardScreen(
    navController: NavController,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Manejar navegación
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is LeaderboardViewModel.NavigationEvent.NavigateToUserProfile -> {
                // TODO: Navegar a perfil de usuario
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
                LeaderboardHeader(
                    currentUser = uiState.currentUser,
                    onSettingsClick = {
                        navController.navigate(Routes.Settings)
                    },
                    onAvatarClick = { navController.navigate(Routes.Profile) }
                )
            },
            bottomBar = {
                SharedBottomNavigation(
                    navController = navController,
                    currentRoute = Routes.Leaderboard
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
                // Mostrar error si lo hay
                if (!uiState.error.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = uiState.error ?: "Error desconocido", color = Color.White)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.onEvent(LeaderboardUiEvent.RefreshLeaderboard) }) {
                                Text(text = "Reintentar")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        // Tabs
                        item {
                            Text(
                                text = if (uiState.selectedTab.name == "WEEKLY") {
                                    "Top semanal"
                                } else {
                                    "Top mensual"
                                },
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Top 3
                        item {
                            TopThreeCard(
                                users = uiState.topThree,
                                onUserClick = { userId ->
                                    viewModel.onEvent(LeaderboardUiEvent.UserClicked(userId))
                                }
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Resto de usuarios
                        items(uiState.otherUsers) { user ->
                            LeaderboardItem(
                                user = user,
                                onClick = {
                                    navController.navigate(Routes.Profile)
                                    //viewModel.onEvent(LeaderboardUiEvent.UserClicked(user.id))
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}