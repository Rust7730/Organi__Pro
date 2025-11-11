package com.e243768.videopractice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.e243768.videopractice.fonts.fontStyles
import com.e243768.videopractice.navigation.Routes
import com.e243768.videopractice.styles.componentStyles
import com.e243768.videopractice.views.loading
import com.e243768.videopractice.views.login
import com.e243768.videopractice.views.vista

@Composable
fun vistas() {
    val navController = rememberNavController()
    val styles = fontStyles()
    val compStyles = componentStyles()
    NavHost(navController = navController, startDestination = Routes.Loading, builder = {
        composable(Routes.Intro) {
            vista(navController, styles)
        }
        composable(Routes.Login) {
            login(navController, styles, compStyles)
        }
        composable(Routes.Loading) {
            loading(navController)
        }


    })

}
