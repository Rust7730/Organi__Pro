package com.e243768.organipro_.presentation.views.tasks.weekly.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e243768.organipro_.presentation.viewmodels.tasks.weekly.WeeklyTaskData

@Composable
fun WeeklyDayCell(
    taskData: WeeklyTaskData?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (taskData?.task != null) {
                    Color(0xFF7B3DFF)
                } else {
                    Color(0xFF2A214D)
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Mostrar indicador de progreso si existe
        if (taskData?.task != null && taskData.progress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(taskData.progress)
                    .height(4.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFF8A5CFF))
            )
        }

        // Mostrar letra inicial si hay tarea
        taskData?.task?.let { task ->
            Text(
                text = task.title.first().toString().uppercase(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}