package com.e243768.organipro_.presentation.views.tasks.weekly

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState // Importante importar esto
import com.e243768.organipro_.core.util.DateUtils
import com.e243768.organipro_.presentation.components.SharedBottomNavigation
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.viewmodels.tasks.weekly.WeeklyTasksUiEvent
import com.e243768.organipro_.presentation.viewmodels.tasks.weekly.WeeklyTasksViewModel
import com.e243768.organipro_.presentation.views.home.components.TaskCard
import com.e243768.organipro_.presentation.views.tasks.weekly.components.WeeklyGrid
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyTasksScreen(
    navController: NavController,
    viewModel: WeeklyTasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // 1. Obtenemos la ruta actual para la BottomBar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(navigationEvent) {
        when (val event = navigationEvent) {
            is WeeklyTasksViewModel.NavigationEvent.NavigateToCreateTask -> {
                navController.navigate(Routes.getCreateTaskRoute())
                viewModel.onNavigationHandled()
            }
            is WeeklyTasksViewModel.NavigationEvent.NavigateToTaskDetail -> {
                navController.navigate(Routes.getTaskDetailRoute(event.taskId))
                viewModel.onNavigationHandled()
            }
            null -> {}
            else -> {}
        }
    }

    Scaffold(
        // 2. Pasamos el currentRoute al componente
        bottomBar = {
            SharedBottomNavigation(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(WeeklyTasksUiEvent.CreateTaskClicked) },
                containerColor = Color(0xFF7A5EFF),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Tarea")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GradientWithStarsBackground()

            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onEvent(WeeklyTasksUiEvent.PreviousWeekClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Anterior", tint = Color.White)
                    }

                    Text(
                        text = uiState.weekLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    IconButton(onClick = { viewModel.onEvent(WeeklyTasksUiEvent.NextWeekClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Siguiente", tint = Color.White)
                    }
                }

                // Grid Semanal
                WeeklyGrid(
                    days = uiState.days,
                    selectedDate = uiState.selectedDate,
                    onDayClick = { viewModel.onEvent(WeeklyTasksUiEvent.DayClicked(it)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Lista de tareas
                val tasksForSelectedDay = uiState.days.find {
                    DateUtils.isSameDay(it.date, uiState.selectedDate)
                }?.tasks ?: emptyList()

                Text(
                    text = "Tareas del ${DateUtils.formatDayNameShort(uiState.selectedDate)} ${DateUtils.formatDayNumber(uiState.selectedDate)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (tasksForSelectedDay.isEmpty()) {
                        item {
                            Text(
                                text = "No hay tareas para este día",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(tasksForSelectedDay) { task ->
                            TaskCard(
                                task = task,
                                onClick = { viewModel.onEvent(WeeklyTasksUiEvent.TaskClicked(task)) }
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF7A5EFF)
                )
            }
        }
    }
}