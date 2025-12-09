package com.e243768.organipro_.presentation.views.tasks.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.e243768.organipro_.domain.model.Priority
import com.e243768.organipro_.presentation.components.AppButton
import com.e243768.organipro_.presentation.components.AppTextField
import com.e243768.organipro_.presentation.viewmodels.tasks.create.CreateTaskUiEvent
import com.e243768.organipro_.presentation.viewmodels.tasks.create.CreateTaskViewModel
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    navController: NavController,
    viewModel: CreateTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // --- Selector de Archivos ---
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Ignorar si el URI no soporta persistencia
            }
            viewModel.onEvent(CreateTaskUiEvent.AttachmentSelected(it))
        }
    }

    // Navegación
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is CreateTaskViewModel.NavigationEvent.NavigateBack -> {
                navController.popBackStack()
                viewModel.onNavigationHandled()
            }
            null -> {}
        }
    }

    // Formateadores y Diálogos
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val calendar = Calendar.getInstance()

    // Definición de variables de texto formateado
    val formattedDate = uiState.dueDate?.let { dateFormat.format(it) } ?: ""
    val formattedTime = uiState.dueTime?.let { timeFormat.format(Date(it)) } ?: ""

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Crear una instancia fresca para evitar efectos secundarios
            val newDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // Opcional: Limpiar la hora para que sea medianoche pura si solo te importa la fecha
                // set(Calendar.HOUR_OF_DAY, 0)
                // set(Calendar.MINUTE, 0)
            }.time

            viewModel.onEvent(CreateTaskUiEvent.DateChanged(newDate))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)

    )
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            // Calcular milisegundos desde el inicio del día
            val timeInMillis = (hourOfDay * 3600000L) + (minute * 60000L)
            viewModel.onEvent(CreateTaskUiEvent.TimeChanged(timeInMillis))
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )
    Box(modifier = Modifier.fillMaxSize()) {
        GradientWithStarsBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (uiState.isEditing) "Editar Tarea" else "Nueva Tarea",
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.onEvent(CreateTaskUiEvent.TitleChanged(it)) },
                    label = "Título de la tarea",
                    placeholder = "Ej. Estudiar Matemáticas"
                )

                AppTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onEvent(CreateTaskUiEvent.DescriptionChanged(it)) },
                    label = "Descripción",
                    singleLine = false,
                    maxLines = 3
                )

                AppTextField(
                    value = uiState.category,
                    onValueChange = { viewModel.onEvent(CreateTaskUiEvent.CategoryChanged(it)) },
                    label = "Categoría",
                    placeholder = "Trabajo, Salud, etc."
                )

                Text("Prioridad", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { priority ->
                        FilterChip(
                            selected = uiState.priority == priority,
                            onClick = { viewModel.onEvent(CreateTaskUiEvent.PriorityChanged(priority)) },
                            label = { Text(priority.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Fecha y Hora (Corregido)
                // Fecha y Hora
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // --- Campo Fecha ---
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = formattedDate,
                            onValueChange = {},
                            label = { Text("Fecha") },
                            readOnly = true,
                            enabled = false, // Deshabilitado visualmente
                            trailingIcon = {
                                Icon(Icons.Default.CalendarToday, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.White,
                                disabledBorderColor = Color.White.copy(alpha = 0.5f),
                                disabledLabelColor = Color.White.copy(alpha = 0.7f),
                                disabledTrailingIconColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                        // Este Box transparente cubre el TextField y captura el click
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { datePickerDialog.show() }
                        )
                    }

                    // --- Campo Hora ---
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = formattedTime,
                            onValueChange = {},
                            label = { Text("Hora") },
                            readOnly = true,
                            enabled = false, // Deshabilitado visualmente
                            trailingIcon = {
                                Icon(Icons.Default.Schedule, contentDescription = null)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.White,
                                disabledBorderColor = Color.White.copy(alpha = 0.5f),
                                disabledLabelColor = Color.White.copy(alpha = 0.7f),
                                disabledTrailingIconColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                        // Este Box transparente cubre el TextField y captura el click
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { timePickerDialog.show() }
                        )
                    }
                }

                Text("Adjuntos", color = Color.White, style = MaterialTheme.typography.titleMedium)

                OutlinedButton(
                    onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Adjuntar Archivo o Imagen")
                }

                if (uiState.selectedAttachments.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.selectedAttachments) { uri ->
                            AttachmentChip(uri = uri) {
                                viewModel.onEvent(CreateTaskUiEvent.AttachmentRemoved(uri))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.error != null) {
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                }

                AppButton(
                    text = if (uiState.isLoading) "Guardando..." else "Crear Tarea",
                    onClick = { viewModel.onEvent(CreateTaskUiEvent.SaveTaskClicked) },
                    enabled = !uiState.isLoading
                )
            }
        }
    }
}

@Composable
fun AttachmentChip(uri: Uri, onRemove: () -> Unit) {
    Surface(
        color = Color(0xFF2A214D),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.widthIn(max = 120.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = uri.lastPathSegment?.split("/")?.last() ?: "Archivo",
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Quitar adjunto",
                tint = Color(0xFFFF6B6B),
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() }
            )
        }
    }
}
