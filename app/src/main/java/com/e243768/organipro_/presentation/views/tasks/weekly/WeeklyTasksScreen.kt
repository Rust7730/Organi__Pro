// presentation/views/tasks/weekly/WeeklyTasksScreen.kt
package com.e243768.organipro_.presentation.views.tasks.weekly

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
import com.e243768.organipro_.presentation.viewmodels.tasks.weekly.WeeklyTasksUiEvent
import com.e243768.organipro_.presentation.viewmodels.tasks.weekly.WeeklyTasksViewModel
import com.e243768.organipro_.presentation.views.tasks.daily.components.TaskTabs
import com.e243768.organipro_.presentation.views.tasks.weekly.components.WeeklyGrid
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyTasksScreen(
    navController: NavController,
    viewModel: WeeklyTasksViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Manejar navegación
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is WeeklyTasksViewModel.NavigationEvent.NavigateBack -> {
                navController.popBackStack()
                viewModel.onNavigationHandled()
            }
            is WeeklyTasksViewModel.NavigationEvent.NavigateToDaily -> {
                navController.navigate(Routes.DailyTasks) {
                    popUpTo(Routes.WeeklyTasks) { inclusive = true }
                }
                viewModel.onNavigationHandled()
            }
            is WeeklyTasksViewModel.NavigationEvent.NavigateToMonthly -> {
                navController.navigate(Routes.MonthlyTasks)
                viewModel.onNavigationHandled()
            }
            is WeeklyTasksViewModel.NavigationEvent.NavigateToTaskDetail -> {
                val event = navigationEvent as WeeklyTasksViewModel.NavigationEvent.NavigateToTaskDetail
                navController.navigate(Routes.taskDetail(event.taskId))
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
                                text = uiState.weekRange,
                                fontSize = 12.sp,
                                color = Color(0xFFB0AEC3)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(WeeklyTasksUiEvent.BackClicked)
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
                    currentRoute = Routes.WeeklyTasks
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
                        viewModel.onEvent(WeeklyTasksUiEvent.TabSelected(tab))
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
                                WeeklyGrid(
                                    uiState = uiState,
                                    onTaskClick = { day, time ->
                                        // Obtener la tarea y hacer click
                                        val task = uiState.weeklyTasks[day]?.get(time)?.task
                                        task?.let {
                                            viewModel.onEvent(WeeklyTasksUiEvent.TaskClicked(it))
                                        }
                                    },
                                    onSlotClick = { day, time ->
                                        viewModel.onEvent(
                                            WeeklyTasksUiEvent.DaySlotClicked(day, time)
                                        )
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