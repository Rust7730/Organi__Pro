// presentation/views/auth/login/LoginScreen.kt
package com.e243768.organipro_.presentation.views.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.e243768.organipro_.presentation.components.AppButton
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.viewmodels.auth.login.LoginUiEvent
import com.e243768.organipro_.presentation.viewmodels.auth.login.LoginViewModel
import com.e243768.organipro_.presentation.views.auth.login.components.LoginForm
import com.e243768.organipro_.presentation.views.auth.login.components.LoginHeader
import com.e243768.organipro_.ui.theme.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Manejar navegación
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is LoginViewModel.NavigationEvent.NavigateToHome -> {
                navController.navigate(Routes.Home) {
                    popUpTo(Routes.Login) { inclusive = true }
                }
                viewModel.onNavigationHandled()
            }
            is LoginViewModel.NavigationEvent.NavigateToSignUp -> {
                navController.navigate(Routes.SignUp)
                viewModel.onNavigationHandled()
            }
            null -> { /* No hacer nada */ }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GradientWithStarsBackground()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header
                LoginHeader()

                Spacer(modifier = Modifier.height(50.dp))

                // Formulario
                LoginForm(
                    uiState = uiState,
                    onEmailChange = {
                        viewModel.onEvent(LoginUiEvent.EmailChanged(it))
                    },
                    onPasswordChange = {
                        viewModel.onEvent(LoginUiEvent.PasswordChanged(it))
                    },
                    onPasswordToggle = {
                        viewModel.onEvent(LoginUiEvent.TogglePasswordVisibility)
                    }
                )

                // Mensaje de error
                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.error!!,
                        color = Color(0xFFFF6B6B),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de Login
                AppButton(
                    text = "Inicia sesión",
                    onClick = {
                        viewModel.onEvent(LoginUiEvent.LoginClicked)
                    },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Link a registro
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿No tienes cuenta? ",
                        color = Color(0xFFB0AEC3),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "¡Regístrate!",
                        color = Color(0xFF8A5CFF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            viewModel.onEvent(LoginUiEvent.SignUpClicked)
                        }
                    )
                }
            }
        }
    }
}