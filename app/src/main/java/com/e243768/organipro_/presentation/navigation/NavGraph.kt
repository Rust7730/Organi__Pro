package com.e243768.organipro_.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.e243768.organipro_.presentation.views.auth.login.LoginScreen
import com.e243768.organipro_.presentation.views.auth.signup.SignUpScreen
import com.e243768.organipro_.presentation.views.home.HomeScreen
import com.e243768.organipro_.presentation.views.intro.IntroScreen
import com.e243768.organipro_.presentation.views.splash.LoadingScreen
import com.e243768.organipro_.presentation.views.tasks.daily.DailyTasksScreen
import com.e243768.organipro_.presentation.views.tasks.weekly.WeeklyTasksScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.DailyTasks
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

        composable(Routes.SignUp) {
            SignUpScreen(navController)
        }

        composable(Routes.Home) {
            HomeScreen(navController = navController)
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

        composable(Routes.DailyTasks) {
            DailyTasksScreen(navController)
        }

        composable(Routes.WeeklyTasks) {
            WeeklyTasksScreen(navController)

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