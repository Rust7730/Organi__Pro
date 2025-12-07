// presentation/views/home/HomeScreen.kt
package com.e243768.organipro_.presentation.views.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.e243768.organipro_.presentation.components.SharedBottomNavigation
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.viewmodels.home.HomeUiEvent  // ← Importar correctamente
import com.e243768.organipro_.presentation.viewmodels.home.HomeViewModel
import com.e243768.organipro_.presentation.views.home.components.HomeHeader
import com.e243768.organipro_.presentation.views.home.components.TaskCard
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is HomeViewModel.NavigationEvent.NavigateToSettings -> {
                navController.navigate(Routes.Settings)
                viewModel.onNavigationHandled()
            }
            is HomeViewModel.NavigationEvent.NavigateToRoute -> {
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
                HomeHeader(
                    userName = uiState.userName,
                    userLevel = uiState.userLevel,
                    streak = uiState.streak,
                    avatarResId = uiState.avatarResId,
                    onSettingsClick = {
                        viewModel.onEvent(HomeUiEvent.SettingsClicked)  // ✅ Correcto
                    }
                )
            },
            bottomBar = {
                SharedBottomNavigation(
                    navController = navController,
                    currentRoute = Routes.Home
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                } else {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tareas para hoy:",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(uiState.todayTasks.size) { index ->
                        val task = uiState.todayTasks[index]
                        TaskCard(
                            task = task,
                            onClick = {
                                viewModel.onEvent(HomeUiEvent.TaskClicked(task))  // ✅ Correcto
                            },
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Tareas para esta semana:",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(uiState.weekTasks.size) { index ->
                        val task = uiState.weekTasks[index]
                        TaskCard(
                            task = task,
                            onClick = {
                                viewModel.onEvent(HomeUiEvent.TaskClicked(task))  // ✅ Correcto
                            },
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }
    }
}