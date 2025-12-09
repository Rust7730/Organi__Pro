package com.e243768.organipro_.presentation.views.tasks.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel // <--- IMPORTANTE: Usar hiltViewModel
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
    taskId: String, // <--- AGREGADO: Recibir el ID
    viewModel: TaskDetailViewModel = hiltViewModel() // <--- AGREGADO: Usar Hilt
) {
    // Cargar la tarea al entrar a la pantalla
    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is TaskDetailViewModel.NavigationEvent.NavigateBack -> {
                navController.popBackStack()
                viewModel.onNavigationHandled()
            }
            null -> { }
            is TaskDetailViewModel.NavigationEvent.NavigateToEdit -> TODO()
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
                        viewModel.onEvent(TaskDetailUiEvent.EditTaskClicked)
                    },
                    onDeleteClick = {
                        viewModel.onEvent(TaskDetailUiEvent.DeleteTaskClicked)
                    }
                )
            },
            bottomBar = {
                // Opcional: Mostrar navegación o botón de completar
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
            } else if (uiState.error != null) {
                // Mostrar error si la tarea no carga
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.error ?: "Error desconocido", color = Color.Red)
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
                            },
                            // Aquí podrías agregar un botón para completar la tarea si no está en el Header
                        )

                        // Ejemplo de botón completar al final
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.onEvent(TaskDetailUiEvent.CompleteTaskClicked) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("Completar Tarea (+100 XP)")
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}