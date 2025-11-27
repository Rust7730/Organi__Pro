// presentation/views/tasks/daily/components/TimeSlot.kt
package com.e243768.organipro_.presentation.views.tasks.daily.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e243768.organipro_.presentation.viewmodels.tasks.daily.TimeSlotData

@Composable
fun TimeSlot(
    timeSlotData: TimeSlotData,
    onTaskClick: () -> Unit,
    onSlotClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hora
        Text(
            text = timeSlotData.time,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(80.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Contenedor de tarea o slot vacío
        Box(
            modifier = Modifier
                .weight(1f)
                .height(if (timeSlotData.task != null) 80.dp else 60.dp)
        ) {
            if (timeSlotData.task != null) {
                DailyTaskCard(
                    task = timeSlotData.task,
                    progress = timeSlotData.progress,
                    onClick = onTaskClick
                )
            } else {
                // Slot vacío clickeable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color(0xFF2A214D),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onSlotClick)
                )
            }
        }
    }
}