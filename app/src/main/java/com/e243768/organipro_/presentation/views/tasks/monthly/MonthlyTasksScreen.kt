// presentation/views/tasks/monthly/MonthlyTasksScreen.kt
package com.e243768.organipro_.presentation.views.tasks.monthly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.e243768.organipro_.presentation.components.SharedBottomNavigation
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.viewmodels.tasks.monthly.MonthlyTasksUiEvent
import com.e243768.organipro_.presentation.viewmodels.tasks.monthly.MonthlyTasksViewModel
import com.e243768.organipro_.presentation.views.tasks.daily.components.TaskTabs
import com.e243768.organipro_.presentation.views.tasks.monthly.components.MonthlyGrid
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyTasksScreen(
    navController: NavController,
    viewModel: MonthlyTasksViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Manejar navegación
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is MonthlyTasksViewModel.NavigationEvent.NavigateBack -> {
                navController.popBackStack()
                viewModel.onNavigationHandled()
            }
            is MonthlyTasksViewModel.NavigationEvent.NavigateToDaily -> {
                navController.navigate(Routes.DailyTasks) {
                    popUpTo(Routes.MonthlyTasks) { inclusive = true }
                }
                viewModel.onNavigationHandled()
            }
            is MonthlyTasksViewModel.NavigationEvent.NavigateToWeekly -> {
                navController.navigate(Routes.WeeklyTasks) {
                    popUpTo(Routes.MonthlyTasks) { inclusive = true }
                }
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
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Tareas",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = uiState.currentMonth,
                                fontSize = 12.sp,
                                color = Color(0xFFB0AEC3)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(MonthlyTasksUiEvent.BackClicked)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                SharedBottomNavigation(
                    navController = navController,
                    currentRoute = Routes.MonthlyTasks
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Tabs
                TaskTabs(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = { tab ->
                        viewModel.onEvent(MonthlyTasksUiEvent.TabSelected(tab))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    // Contenedor con fondo semi-gris
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color(0xFF2A214D).copy(alpha = 0.5f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(16.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                MonthlyGrid(
                                    uiState = uiState,
                                    onDayClick = { day ->
                                        viewModel.onEvent(MonthlyTasksUiEvent.DayClicked(day))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}