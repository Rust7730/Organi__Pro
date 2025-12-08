package com.e243768.organipro_.presentation.navigation

object Routes {
    // Auth Flow
    const val Splash = "splash"
    const val Intro = "intro"
    const val Login = "login"
    const val SignUp = "signup"

    // Main App Flow
    const val Home = "home"
    const val Leaderboard = "leaderboard"
    const val Profile = "profile"
    const val Settings = "settings"

    // Tasks Flow
    const val DailyTasks = "daily_tasks"
    const val WeeklyTasks = "weekly_tasks"
    const val MonthlyTasks = "monthly_tasks"
    const val TaskDetail = "task_detail/{taskId}"

    const val CreateTask = "create_task?time={time}"

    // Helper para TaskDetail con parámetro
    fun taskDetail(taskId: String) = "task_detail/$taskId"
    fun getTaskDetailRoute(taskId: String) = "task_detail/$taskId"
    fun getCreateTaskRoute(time: String = "") = "create_task?time=$time"
}