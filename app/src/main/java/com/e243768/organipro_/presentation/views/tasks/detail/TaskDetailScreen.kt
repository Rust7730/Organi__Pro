// presentation/views/tasks/detail/TaskDetailScreen.kt
package com.e243768.organipro_.presentation.views.tasks.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.e243768.organipro_.presentation.components.SharedBottomNavigation
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.viewmodels.tasks.detail.TaskDetailUiEvent
import com.e243768.organipro_.presentation.viewmodels.tasks.detail.TaskDetailViewModel
import com.e243768.organipro_.presentation.views.tasks.detail.components.TaskDetailContent
import com.e243768.organipro_.presentation.views.tasks.detail.components.TaskDetailHeader
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground

@Composable
fun TaskDetailScreen(
    navController: NavController,
    viewModel: TaskDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Manejar navegación
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is TaskDetailViewModel.NavigationEvent.NavigateBack -> {
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
            topBar = {
                TaskDetailHeader(
                    onBackClick = {
                        viewModel.onEvent(TaskDetailUiEvent.BackClicked)
                    },
                    onEditClick = {
                        viewModel.onEvent(TaskDetailUiEvent.EditClicked)
                    },
                    onDeleteClick = {
                        viewModel.onEvent(TaskDetailUiEvent.DeleteClicked)
                    }
                )
            },
            bottomBar = {
                SharedBottomNavigation(
                    navController = navController,
                    currentRoute = Routes.TaskDetail
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
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        TaskDetailContent(
                            uiState = uiState,
                            onAttachmentClick = { attachmentId ->
                                viewModel.onEvent(
                                    TaskDetailUiEvent.AttachmentClicked(attachmentId)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}