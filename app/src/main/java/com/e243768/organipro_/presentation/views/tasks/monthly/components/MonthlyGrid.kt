package com.e243768.organipro_.presentation.views.tasks.monthly.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.e243768.organipro_.presentation.viewmodels.tasks.monthly.MonthlyDayData
import java.util.Date

@Composable
fun MonthlyGrid(
    days: List<MonthlyDayData>,
    onDayClick: (Date) -> Unit
) {
    Column {
        // Cabecera de días de la semana (L M M J V S D)
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            val weekDays = listOf("DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB")
            weekDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(days) { dayData ->
                MonthlyDayCell(
                    dayData = dayData,
                    onClick = { dayData.date?.let { onDayClick(it) } }
                )
            }
        }
    }
}