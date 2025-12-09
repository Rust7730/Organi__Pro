package com.e243768.organipro_.presentation.views.auth.signup.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e243768.organipro_.presentation.components.AppTextField
// Asegúrate de que este import coincida con tu carpeta (a veces es 'singup' o 'signup' según tus archivos)
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
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // --- Campo de Nombre ---
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
            // Corrección: Usar KeyboardOptions
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = !uiState.isNameValid && uiState.name.isNotBlank(),
            errorMessage = if (!uiState.isNameValid && uiState.name.isNotBlank()) {
                "Mínimo 2 caracteres"
            } else null
        )

        // --- Campo de Alias ---
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
            // Corrección: Usar KeyboardOptions
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            isError = !uiState.isAliasValid && uiState.alias.isNotBlank(),
            errorMessage = if (!uiState.isAliasValid && uiState.alias.isNotBlank()) {
                "Mínimo 3 caracteres"
            } else null
        )

        // --- Campo de Email ---
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
            // Corrección: Usar KeyboardOptions con tipo Email
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = !uiState.isEmailValid && uiState.email.isNotBlank(),
            errorMessage = if (!uiState.isEmailValid && uiState.email.isNotBlank()) {
                "Email inválido"
            } else null
        )

        // --- Campo de Contraseña ---
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
            // Corrección: Usar KeyboardOptions con tipo Password
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            // Corrección: Transformación visual para ocultar caracteres
            visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            // Corrección: Botón del ojo como trailingIcon
            trailingIcon = {
                val image = if (uiState.passwordVisible)
                    Icons.Default.Visibility
                else
                    Icons.Default.VisibilityOff

                IconButton(onClick = onPasswordToggle) {
                    Icon(imageVector = image, contentDescription = "Alternar visibilidad contraseña")
                }
            },
            isError = !uiState.isPasswordValid && uiState.password.isNotBlank(),
            errorMessage = if (!uiState.isPasswordValid && uiState.password.isNotBlank()) {
                "Mínimo 6 caracteres"
            } else null
        )
    }
}