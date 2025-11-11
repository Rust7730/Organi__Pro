package com.e243768.videopractice.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.e243768.videopractice.navigation.Routes
import com.e243768.videopractice.views.loading
import com.e243768.videopractice.views.login
import com.e243768.videopractice.views.vista

@Composable
fun vistas() {



        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Routes.Loading, builder = {
            composable(Routes.Intro) {
                vista(navController)
            }
            composable(Routes.Login) {
                login()
            }
            composable(Routes.Loading) {
                loading(navController)
            }


        })

}
