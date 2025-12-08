package com.e243768.organipro_.presentation.views.tasks.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.e243768.organipro_.domain.model.Priority
import com.e243768.organipro_.presentation.components.AppButton
import com.e243768.organipro_.presentation.components.AppTextField
import com.e243768.organipro_.presentation.viewmodels.tasks.create.CreateTaskUiEvent
import com.e243768.organipro_.presentation.viewmodels.tasks.create.CreateTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is CreateTaskViewModel.NavigationEvent.NavigateBack -> {
                onNavigateBack()
                viewModel.onNavigationHandled()
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Tarea", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(CreateTaskUiEvent.BackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF210535))
            )
        },
        containerColor = Color(0xFF210535)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            AppTextField(
                value = uiState.title,
                onValueChange = { viewModel.onEvent(CreateTaskUiEvent.TitleChanged(it)) },
                placeholder = "Ej: Diseñar interfaz"
            )

            // Descripción
            AppTextField(
                value = uiState.description,
                onValueChange = { viewModel.onEvent(CreateTaskUiEvent.DescriptionChanged(it)) },
                placeholder = "Detalles adicionales..."
            )

            // Prioridad
            Text("Prioridad", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.values().forEach { priority ->
                    FilterChip(
                        selected = uiState.priority == priority,
                        onClick = { viewModel.onEvent(CreateTaskUiEvent.PrioritySelected(priority)) },
                        label = { Text(priority.name) }, // O usar priority.displayName si existe
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF7A5EFF),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF2A214D),
                            labelColor = Color.LightGray
                        )
                    )
                }
            }

            // Puntos
            AppTextField(
                value = uiState.points,
                onValueChange = { viewModel.onEvent(CreateTaskUiEvent.PointsChanged(it)) },
                placeholder = "100"
            )

            // Hora
            AppTextField(
                value = uiState.scheduledTime,
                onValueChange = { viewModel.onEvent(CreateTaskUiEvent.TimeChanged(it)) },
                placeholder = "HH:mm"
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AppButton(
                text = "Guardar Tarea",
                onClick = { viewModel.onEvent(CreateTaskUiEvent.SaveClicked) },
                isLoading = uiState.isLoading,
                enabled = !uiState.isLoading
            )
        }
    }
}