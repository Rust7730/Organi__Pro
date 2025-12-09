package com.e243768.organipro_.presentation.views.tasks.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e243768.organipro_.domain.model.Priority
import com.e243768.organipro_.presentation.viewmodels.tasks.detail.TaskDetailUiState

@Composable
fun TaskDetailContent(
    uiState: TaskDetailUiState,
    onAttachmentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Extraemos la tarea del estado. Si es nula, no mostramos nada.
    val task = uiState.task ?: return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Título
        Text(
            text = task.title, // Acceso correcto a través de 'task'
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Prioridad y Puntos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Lógica de color basada en el Enum Priority
                val priorityColor = when (task.priority) {
                    Priority.ALTA -> Color(0xFFFF6B6B)   // Rojo
                    Priority.MEDIA -> Color(0xFFFFC700) // Amarillo
                    Priority.BAJA -> Color(0xFF4CAF50)    // Verde
                }

                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Prioridad",
                    tint = priorityColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Prioridad: ",
                    color = Color.White,
                    fontSize = 14.sp
                )
                // Mapeo del nombre del Enum a Español
                Text(
                    text = when (task.priority) {
                        Priority.ALTA -> "Alta"
                        Priority.MEDIA -> "Media"
                        Priority.BAJA -> "Baja"
                    },
                    color = priorityColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Puntos (Calculado o fijo, ya que no viene directo en Task)
            Text(
                text = "100 XP",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Descripción
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = "Descripción",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Descripción:",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = task.description, // Acceso correcto
            color = Color(0xFFB0AEC3),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            color = Color(0xFF8A5CFF).copy(alpha = 0.3f),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Archivos adjuntos
        // Nota: Actualmente 'Task' no tiene lista de adjuntos.
        // Dejamos esto preparado para cuando actualices el modelo.
        /* if (task.attachments.isNotEmpty()) {
             // ... Tu código de adjuntos ...
             // Por ahora lo comentamos para que compile
        }
        */
        // Mensaje temporal si quieres mostrar algo
        Text(
            text = "Sin archivos adjuntos",
            color = Color.Gray,
            fontSize = 12.sp
        )
        // ... (código previo)

        // Sección de Archivos Adjuntos
        if (uiState.task?.attachments?.isNotEmpty() == true) { // Verificación segura
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Description, // O AttachFile
                    contentDescription = "Archivos",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Archivos adjuntos:",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Iterar y mostrar
            uiState.task.attachments.forEach { attachment ->
                // Asegúrate de tener un componente AttachmentItem o usa este genérico:
                AttachmentItem(
                    attachment = attachment,
                    onClick = { onAttachmentClick(attachment.id) } // El ID que pasas aquí
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}