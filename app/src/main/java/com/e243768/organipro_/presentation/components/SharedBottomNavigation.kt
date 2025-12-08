// presentation/components/SharedBottomNavigation.kt
package com.e243768.organipro_.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.e243768.organipro_.presentation.navigation.Routes

@Composable
fun SharedBottomNavigation(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        containerColor = Color(0xFF2A214D).copy(alpha = 0.95f),
        modifier = modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        val navItems = listOf(
            BottomNavItemData("Tablero", Icons.Default.BarChart, Routes.Leaderboard),
            BottomNavItemData("Hogar", Icons.Default.Home, Routes.Home),
            BottomNavItemData("Tareas", Icons.AutoMirrored.Filled.List, Routes.DailyTasks),
            BottomNavItemData("Perfil", Icons.Default.Person, Routes.Profile)
        )

        navItems.forEachIndexed { index, item ->
            BottomNavItem(
                label = item.label,
                icon = item.icon,
                isSelected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.Home) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )

            // Espaciador en el medio para el FAB (si lo tienes en el futuro)
            if (index == 1) {
                Spacer(Modifier.width(72.dp))
            }
        }
    }
}

private data class BottomNavItemData(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
private fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (isSelected) Color.White else Color(0xFFB0AEC3)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}