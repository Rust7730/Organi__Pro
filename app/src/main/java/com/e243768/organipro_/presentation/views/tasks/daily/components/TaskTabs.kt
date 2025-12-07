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
import com.e243768.organipro_.presentation.viewmodels.tasks.daily.TaskTab

@Composable
fun TaskTabs(
    selectedTab: TaskTab,
    onTabSelected: (TaskTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF2A214D),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TabItem(
            text = "Día",
            isSelected = selectedTab == TaskTab.DAY,
            onClick = { onTabSelected(TaskTab.DAY) },
            modifier = Modifier.weight(1f)
        )

        TabItem(
            text = "Semana",
            isSelected = selectedTab == TaskTab.WEEK,
            onClick = { onTabSelected(TaskTab.WEEK) },
            modifier = Modifier.weight(1f)
        )

        TabItem(
            text = "Mes",
            isSelected = selectedTab == TaskTab.MONTH,
            onClick = { onTabSelected(TaskTab.MONTH) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) Color(0xFF8A5CFF) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}