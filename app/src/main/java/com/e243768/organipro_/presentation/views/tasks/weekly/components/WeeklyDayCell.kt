package com.e243768.organipro_.presentation.views.tasks.weekly.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.e243768.organipro_.presentation.viewmodels.tasks.weekly.WeeklyDayData

@Composable
fun WeeklyDayCell(
    dayData: WeeklyDayData,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    // Definimos colores según selección y si es "hoy"
    val containerColor = when {
        isSelected -> Brush.verticalGradient(listOf(Color(0xFF7A5EFF), Color(0xFF4B2AA5)))
        dayData.isToday -> Brush.verticalGradient(listOf(Color(0xFF2A214D), Color(0xFF2A214D)))
        else -> Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
    }

    val borderColor = if (dayData.isToday && !isSelected) Color(0xFF7A5EFF) else Color.Transparent

    Column(
        modifier = Modifier
            .width(60.dp)
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .clickable { onClick() }
            .then(
                if (dayData.isToday) Modifier.background(borderColor.copy(alpha = 0.1f)) else Modifier
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayData.dayName, // "Lun"
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) Color.White else Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = dayData.dayNumber, // "12"
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Barra de progreso mini
        if (dayData.tasks.isNotEmpty()) {
            LinearProgressIndicator(
                progress = { dayData.progress },
                modifier = Modifier
                    .width(30.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (isSelected) Color.White else Color(0xFF7A5EFF),
                trackColor = Color.Black.copy(alpha = 0.3f),
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}