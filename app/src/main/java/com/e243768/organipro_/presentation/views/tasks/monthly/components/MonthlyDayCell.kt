package com.e243768.organipro_.presentation.views.tasks.monthly.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.e243768.organipro_.presentation.viewmodels.tasks.monthly.MonthDayData

@Composable
fun MonthlyDayCell(
    dayData: MonthDayData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Si el día es 0, es una celda vacía
    if (dayData.dayNumber == 0) {
        Box(
            modifier = modifier
                .size(48.dp)
        )
        return
    }

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = when {
                    dayData.isSelected -> Color(0xFF8A5CFF) // Día seleccionado
                    dayData.hasTask -> Color(0xFF7B3DFF).copy(alpha = 0.6f) // Día con tareas
                    else -> Color(0xFF2A214D) // Día normal
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayData.dayNumber.toString(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (dayData.isCurrentDay || dayData.isSelected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
            )

            // Indicador de día actual
            if (dayData.isCurrentDay && !dayData.isSelected) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8A5CFF))
                )
            }

            // Indicador de tareas
            if (dayData.hasTask && dayData.taskCount > 0) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "•",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 8.sp
                )
            }
        }
    }
}