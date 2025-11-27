package com.e243768.organipro_.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavManager() {
    val navController = rememberNavController()
    AppNavGraph(
        navController = navController,
    )
}