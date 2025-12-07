package com.e243768.organipro_.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.e243768.organipro_.presentation.views.auth.login.LoginScreen
import com.e243768.organipro_.presentation.views.auth.signup.SignUpScreen
import com.e243768.organipro_.presentation.views.home.HomeScreen
import com.e243768.organipro_.presentation.views.intro.IntroScreen
import com.e243768.organipro_.presentation.views.leaderboard.LeaderboardScreen
import com.e243768.organipro_.presentation.views.profile.ProfileScreen
import com.e243768.organipro_.presentation.views.settings.SettingsScreen
import com.e243768.organipro_.presentation.views.splash.LoadingScreen
import com.e243768.organipro_.presentation.views.tasks.daily.DailyTasksScreen
import com.e243768.organipro_.presentation.views.tasks.detail.TaskDetailScreen
import com.e243768.organipro_.presentation.views.tasks.monthly.MonthlyTasksScreen
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

        composable(Routes.Leaderboard) {
            LeaderboardScreen(navController)

        }

        composable(Routes.Profile) {
            ProfileScreen(navController)

        }

        composable(Routes.Settings) {
            SettingsScreen(navController)

        }

        composable(Routes.DailyTasks) {
            DailyTasksScreen(navController)
        }

        composable(Routes.WeeklyTasks) {
            WeeklyTasksScreen(navController)

        }

        composable(Routes.MonthlyTasks) {
            MonthlyTasksScreen(navController)

        }

        composable(Routes.TaskDetail) {
                backStackEntry ->
            TaskDetailScreen(navController)        }
    }
}