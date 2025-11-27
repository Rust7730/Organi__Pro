package com.e243768.organipro_.presentation.views.auth.signup.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e243768.organipro_.presentation.components.AppTextField
import com.e243768.organipro_.presentation.viewmodels.auth.signup.SignUpUiState

@Composable
fun SignUpForm(
    uiState: SignUpUiState,
    onNameChange: (String) -> Unit,
    onAliasChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0xFF1E1C2F),
                ambientColor = Color(0xFF1E1C2F),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.5.dp,
                color = Color(0xFFEFEFEF),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(24.dp),
       // horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Campo de Nombre
        Text(
            style = MaterialTheme.typography.headlineMedium,
            text = "Nombre",
            color = Color.White,
            textAlign = TextAlign.Left,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,

            )
        AppTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            placeholder = "Tu nombre.",
            keyboardType = KeyboardType.Text,
            isError = !uiState.isNameValid && uiState.name.isNotBlank(),
            errorMessage = if (!uiState.isNameValid && uiState.name.isNotBlank()) {
                "El nombre debe tener al menos 2 caracteres"
            } else null
        )

        // Campo de Alias
        Text(
            style = MaterialTheme.typography.headlineMedium,
            text = "Nickname",
            color = Color.White,
            textAlign = TextAlign.Left,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,

            )
        AppTextField(
            value = uiState.alias,
            onValueChange = onAliasChange,
            placeholder = "Como te verá la gente.",
            keyboardType = KeyboardType.Text,
            isError = !uiState.isAliasValid && uiState.alias.isNotBlank(),
            errorMessage = if (!uiState.isAliasValid && uiState.alias.isNotBlank()) {
                "El alias debe tener al menos 3 caracteres"
            } else null
        )

        // Campo de Email
        Text(
            style = MaterialTheme.typography.headlineMedium,
            text = "Correo Electrónico",
            color = Color.White,
            textAlign = TextAlign.Left,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,

            )
        AppTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = "correo@dominio.com",
            keyboardType = KeyboardType.Email,
            isError = !uiState.isEmailValid && uiState.email.isNotBlank(),
            errorMessage = if (!uiState.isEmailValid && uiState.email.isNotBlank()) {
                "Email inválido"
            } else null
        )

        // Campo de Contraseña
        Text(
            style = MaterialTheme.typography.headlineMedium,
            text = "Contraseña",
            color = Color.White,
            textAlign = TextAlign.Left,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,

            )
        AppTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = "••••••••••••••••••••••",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            passwordVisible = uiState.passwordVisible,
            onPasswordToggle = onPasswordToggle,
            isError = !uiState.isPasswordValid && uiState.password.isNotBlank(),
            errorMessage = if (!uiState.isPasswordValid && uiState.password.isNotBlank()) {
                "La contraseña debe tener al menos 6 caracteres"
            } else null
        )
    }
}