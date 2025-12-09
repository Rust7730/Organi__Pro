package com.e243768.organipro_.presentation.views.tasks.monthly.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.e243768.organipro_.presentation.viewmodels.tasks.monthly.MonthlyDayData

@Composable
fun MonthlyDayCell(
    dayData: MonthlyDayData,
    onClick: () -> Unit
) {
    if (dayData.date == null) {
        // Celda vacía para padding
        Box(modifier = Modifier.aspectRatio(1f))
        return
    }

    val backgroundColor = if (dayData.isToday) Color(0xFF7A5EFF) else Color.Transparent
    val textColor = if (dayData.isToday) Color.White else Color.White
    val borderColor = if (dayData.hasTasks && !dayData.isToday) Color(0xFF7A5EFF) else Color.Transparent

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .background(backgroundColor, shape = CircleShape)
            .border(1.dp, borderColor, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dayData.dayNumber,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )

            // Indicadores de tareas (puntos)
            if (dayData.hasTasks) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    // Mostrar hasta 3 puntos
                    repeat(minOf(dayData.totalCount, 3)) { index ->
                        val isCompletedDot = index < dayData.completedCount
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(
                                    if (isCompletedDot) Color(0xFF00E676) else Color.White.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}