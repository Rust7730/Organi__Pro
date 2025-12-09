package com.e243768.organipro_.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.e243768.organipro_.presentation.views.auth.login.LoginScreen
import com.e243768.organipro_.presentation.views.auth.signup.SignUpScreen
import com.e243768.organipro_.presentation.views.home.HomeScreen
import com.e243768.organipro_.presentation.views.intro.IntroScreen
import com.e243768.organipro_.presentation.views.leaderboard.LeaderboardScreen
import com.e243768.organipro_.presentation.views.profile.ProfileScreen
import com.e243768.organipro_.presentation.views.settings.SettingsScreen
import com.e243768.organipro_.presentation.views.splash.LoadingScreen
import com.e243768.organipro_.presentation.views.tasks.create.CreateTaskScreen
import com.e243768.organipro_.presentation.views.tasks.daily.DailyTasksScreen
import com.e243768.organipro_.presentation.views.tasks.detail.TaskDetailScreen
import com.e243768.organipro_.presentation.views.tasks.monthly.MonthlyTasksScreen
import com.e243768.organipro_.presentation.views.tasks.weekly.WeeklyTasksScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.Splash
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

        composable(
            route = Routes.TaskDetailArg, // Usamos la constante con /{taskId}
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetailScreen(
                navController = navController,
                taskId = taskId
            )
        }
        composable(
            route = Routes.EditTaskArg, // "edit_task/{taskId}"
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            )
        ) {
            // Hilt inyectará automáticamente el taskId en el SavedStateHandle del ViewModel
            CreateTaskScreen(navController = navController)
        }
        // --- CORRECCIÓN AQUÍ ---
        composable(
            route = Routes.CreateTask,
            arguments = listOf(navArgument("time") { type = NavType.StringType; nullable = true })
        ) {
            CreateTaskScreen(navController = navController)
        }
        composable(
            route = Routes.EditTaskArg,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) {
            CreateTaskScreen(navController = navController)
        }
    }
}