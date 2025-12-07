package com.e243768.organipro_.presentation.views.tasks.monthly.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e243768.organipro_.presentation.viewmodels.tasks.monthly.MonthlyTasksUiState

@Composable
fun MonthlyGrid(
    uiState: MonthlyTasksUiState,
    onDayClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Headers de días
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            uiState.daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(48.dp)
                )
            }
        }

        // Grid de días (7 columnas)
        val rows = (uiState.monthDays.size + 6) / 7 // Calcular número de filas necesarias

        for (rowIndex in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (colIndex in 0 until 7) {
                    val index = rowIndex * 7 + colIndex

                    if (index < uiState.monthDays.size) {
                        val dayData = uiState.monthDays[index]

                        MonthlyDayCell(
                            dayData = dayData,
                            onClick = {
                                if (dayData.dayNumber > 0) {
                                    onDayClick(dayData.dayNumber)
                                }
                            }
                        )
                    } else {
                        // Celda vacía para completar la fila
                        Box(modifier = Modifier.size(48.dp))
                    }

                    // Agregar espaciado entre celdas excepto la última
                    if (colIndex < 6) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
    }
}