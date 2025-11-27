// presentation/views/auth/signup/SignUpScreen.kt
package com.e243768.organipro_.presentation.views.auth.signup


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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.e243768.organipro_.presentation.components.AppButton
import com.e243768.organipro_.presentation.navigation.Routes
import com.e243768.organipro_.presentation.viewmodels.auth.signup.SignUpUiEvent
import com.e243768.organipro_.presentation.viewmodels.auth.signup.SignUpViewModel
import com.e243768.organipro_.presentation.views.auth.signup.components.SignUpForm
import com.e243768.organipro_.presentation.views.auth.signup.components.SignUpHeader
import com.e243768.organipro_.ui.theme.GradientWithStarsBackground
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Manejar navegación
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is SignUpViewModel.NavigationEvent.NavigateToHome -> {
                navController.navigate(Routes.Home) {
                    popUpTo(Routes.SignUp) { inclusive = true }
                }
                viewModel.onNavigationHandled()
            }
            is SignUpViewModel.NavigationEvent.NavigateToLogin -> {
                navController.popBackStack()
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
                SignUpHeader()

                Spacer(modifier = Modifier.height(40.dp))

                // Formulario
                SignUpForm(
                    uiState = uiState,
                    onNameChange = {
                        viewModel.onEvent(SignUpUiEvent.NameChanged(it))
                    },
                    onAliasChange = {
                        viewModel.onEvent(SignUpUiEvent.AliasChanged(it))
                    },
                    onEmailChange = {
                        viewModel.onEvent(SignUpUiEvent.EmailChanged(it))
                    },
                    onPasswordChange = {
                        viewModel.onEvent(SignUpUiEvent.PasswordChanged(it))
                    },
                    onPasswordToggle = {
                        viewModel.onEvent(SignUpUiEvent.TogglePasswordVisibility)
                    }
                )

                // Mensaje de error
                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.error!!,
                        color = Color(0xFFFF6B6B),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de Registro
                AppButton(
                    text = "Inicia sesión",
                    onClick = {
                        viewModel.onEvent(SignUpUiEvent.SignUpClicked)
                    },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.padding(horizontal = 30.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Link a login
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        color = Color(0xFFB0AEC3),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "¡Inicia sesión!",
                        color = Color(0xFF8A5CFF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            viewModel.onEvent(SignUpUiEvent.LoginClicked)
                        }
                    )
                }
            }
        }
    }
}