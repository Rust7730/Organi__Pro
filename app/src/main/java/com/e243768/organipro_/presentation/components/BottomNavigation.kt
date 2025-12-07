package com.e243768.organipro_.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.e243768.organipro_.presentation.views.home.components.BottomNavItem

@Composable
fun BottomNavigation(
    onNavItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf("Hogar") }

    BottomAppBar(
        containerColor = Color(0xFF2A214D).copy(alpha = 0.95f),
        modifier = modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        val navItemsLeft = listOf(
            "Tablero" to Icons.Default.BarChart,
            "Hogar" to Icons.Default.Home
        )
        val navItemsRight = listOf(
            "Tareas" to Icons.AutoMirrored.Filled.List,
            "Perfil" to Icons.Default.Person
        )

        navItemsLeft.forEach { (label, icon) ->
            BottomNavItem(
                label = label,
                icon = icon,
                isSelected = selectedItem == label,
                onClick = {
                    selectedItem = label
                    onNavItemClick(label)
                }
            )
        }

        Spacer(Modifier.width(72.dp))

        navItemsRight.forEach { (label, icon) ->
            BottomNavItem(
                label = label,
                icon = icon,
                isSelected = selectedItem == label,
                onClick = {
                    selectedItem = label
                    onNavItemClick(label)
                }
            )
        }
    }
}