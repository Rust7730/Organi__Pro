package com.e243768.organipro_.presentation.views.tasks.weekly.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.e243768.organipro_.core.util.DateUtils
import com.e243768.organipro_.presentation.viewmodels.tasks.weekly.WeeklyDayData
import java.util.Date

@Composable
fun WeeklyGrid(
    days: List<WeeklyDayData>,
    selectedDate: Date,
    onDayClick: (Date) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(days) { day ->
            val isSelected = DateUtils.isSameDay(day.date, selectedDate)
            WeeklyDayCell(
                dayData = day,
                isSelected = isSelected,
                onClick = { onDayClick(day.date) }
            )
        }
    }
}