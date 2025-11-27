package com.e243768.organipro_.presentation.views.splash


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.e243768.organipro_.R
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.views.splash.components.LoadingAnimation
import com.e243768.organipro_.presentation.viewmodels.splash.SplashViewModel
import com.e243768.organipro_.ui.theme.gradientLigthly

@Composable
fun LoadingScreen(
    navController: NavController,
    viewModel: SplashViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegación automática
    LaunchedEffect(uiState.navigateToIntro, uiState.navigateToHome) {
        when {
            uiState.navigateToIntro -> {
                navController.navigate(Routes.Intro) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            }
            uiState.navigateToHome -> {
                navController.navigate(Routes.Home) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        gradientLigthly()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                Image(
//                    painter = painterResource(R.drawable.noimage),
//                    contentDescription = "App Logo",
//                    modifier = Modifier.width(200.dp)
//                )
//
                Spacer(modifier = Modifier.height(50.dp))

                // Animación de carga
                LoadingAnimation()
            }
        }
    }
}