package com.e243768.organipro_.presentation.views.tasks.weekly.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e243768.organipro_.presentation.viewmodels.tasks.weekly.WeeklyTasksUiState

@Composable
fun WeeklyGrid(
    uiState: WeeklyTasksUiState,
    onTaskClick: (String, String) -> Unit,
    onSlotClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Headers de días
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Espacio para la columna de horarios
            Spacer(modifier = Modifier.width(60.dp))

            uiState.daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(48.dp)
                )
            }
        }

        // Filas de horarios
        uiState.timeSlots.forEach { timeSlot ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Columna de hora
                Text(
                    text = timeSlot,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(60.dp)
                )

                // Celdas de días
                uiState.daysOfWeek.forEach { day ->
                    val taskData = uiState.weeklyTasks[day]?.get(timeSlot)

                    WeeklyDayCell(
                        taskData = taskData,
                        onClick = {
                            if (taskData?.task != null) {
                                onTaskClick(day, timeSlot)
                            } else {
                                onSlotClick(day, timeSlot)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}