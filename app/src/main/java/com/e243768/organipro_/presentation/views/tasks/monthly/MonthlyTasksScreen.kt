package com.e243768.organipro_.presentation.views.tasks.monthly

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState // Importante importar
import com.e243768.organipro_.presentation.components.SharedBottomNavigation
import com.e243768.organipro_.presentation.viewmodels.tasks.monthly.MonthlyTasksUiEvent
import com.e243768.organipro_.presentation.viewmodels.tasks.monthly.MonthlyTasksViewModel
import com.e243768.organipro_.presentation.views.tasks.monthly.components.MonthlyGrid
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyTasksScreen(
    navController: NavController,
    viewModel: MonthlyTasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // 1. Obtenemos la ruta actual
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is MonthlyTasksViewModel.NavigationEvent.NavigateBack -> {
                navController.popBackStack()
                viewModel.onNavigationHandled()
            }
            null -> {}
        }
    }

    Scaffold(
        // 2. Pasamos el parámetro currentRoute
        bottomBar = {
            SharedBottomNavigation(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GradientWithStarsBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Calendario
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.onEvent(MonthlyTasksUiEvent.PreviousMonthClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Anterior", tint = Color.White)
                    }

                    Text(
                        text = uiState.monthLabel,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )

                    IconButton(onClick = { viewModel.onEvent(MonthlyTasksUiEvent.NextMonthClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "Siguiente", tint = Color.White)
                    }
                }

                // Grid Mensual
                MonthlyGrid(
                    days = uiState.days,
                    onDayClick = { viewModel.onEvent(MonthlyTasksUiEvent.DayClicked(it)) }
                )
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