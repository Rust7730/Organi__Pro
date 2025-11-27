package com.e243768.organipro_.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.e243768.organipro_.presentation.views.auth.login.LoginScreen
import com.e243768.organipro_.presentation.views.intro.IntroScreen
import com.e243768.organipro_.presentation.views.splash.LoadingScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.Login
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Splash) {
            LoadingScreen(navController)

        }

        composable(Routes.Intro) {
            IntroScreen(navController)
        }

        composable(Routes.Login) {
            LoginScreen(navController)
        }

        // SignUp (placeholder por ahora)
        composable(Routes.SignUp) {
        }

        // Home (placeholder por ahora)
        composable(Routes.Home) {
        }

        // Leaderboard (placeholder por ahora)
        composable(Routes.Leaderboard) {
        }

        // Profile (placeholder por ahora)
        composable(Routes.Profile) {
        }

        // Settings (placeholder por ahora)
        composable(Routes.Settings) {
        }

        // Daily Tasks (placeholder por ahora)
        composable(Routes.DailyTasks) {
        }

        // Weekly Tasks (placeholder por ahora)
        composable(Routes.WeeklyTasks) {
        }

        // Monthly Tasks (placeholder por ahora)
        composable(Routes.MonthlyTasks) {
        }

        // Task Detail con parámetro (placeholder por ahora)
        composable(Routes.TaskDetail) {
            // val taskId = it.arguments?.getString("taskId") ?: ""
        }
    }
}