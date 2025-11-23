package com.e243768.videopractice.views

import GradientWithStarsBackground
import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.e243768.videopractice.fonts.fontStyles
import com.e243768.videopractice.navigation.Routes
import com.e243768.videopractice.styles.componentStyles
import com.e243768.videopractice.variables.Variables

@Composable
fun signUp(navController: NavController, styles: fontStyles, componentStyles: componentStyles){
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }

    Box(modifier=Modifier.fillMaxSize()){
        GradientWithStarsBackground()
        Scaffold (

            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column  ( modifier = Modifier
                .fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "¡Bienvenido!\nCrea tu perfil.",
                    style = styles.headerStyle
                )

                Spacer(modifier = Modifier.height(15.dp))
                Column(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 50.dp, end = 20.dp, bottom = 10.dp)
                        .padding()
                        .shadow(
                            elevation = 4.dp,
                            spotColor = Color(0xFF1E1C2F),
                            ambientColor = Color(0xFF1E1C2F)
                        )
                        .border(
                            width = 1.5.dp,
                            color = Variables.Color6,
                            shape = RoundedCornerShape(size = 20.dp)
                        )
                        .width(300.dp)
                        .height(470.dp)
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(size = 20.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text("Nombre:", style = styles.smallHighlightedText)
                    Spacer(modifier = Modifier.height(5.dp))
                    TextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = { Text("Tu nombre") },
                        keyboardOptions = KeyboardOptions().copy(keyboardType = KeyboardType.Text),
                        modifier = componentStyles.textFieldModifier,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                    Text("Alias:", style = styles.smallHighlightedText)
                    Spacer(modifier = Modifier.height(5.dp))
                    TextField(
                        value = alias,
                        onValueChange = { alias = it },
                        placeholder = { Text("Cómo los demás te verán") },
                        keyboardOptions = KeyboardOptions().copy(keyboardType = KeyboardType.Text),
                        modifier = componentStyles.textFieldModifier,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Correo electrónico:", style = styles.smallHighlightedText)
                    Spacer(modifier = Modifier.height(5.dp))
                    TextField(
                        value = correo,
                        onValueChange = { correo = it },
                        placeholder = { Text("Correo electrónico") },
                        keyboardOptions = KeyboardOptions().copy(keyboardType = KeyboardType.Email),
                        modifier = componentStyles.textFieldModifier,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Contraseña:", style = styles.smallHighlightedText)
                    Spacer(modifier = Modifier.height(5.dp))
                    TextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        placeholder = { Text("Contraseña") },
                        keyboardOptions = KeyboardOptions().copy(keyboardType = KeyboardType.Password),
                        modifier = componentStyles.textFieldModifier,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                    )
                }

                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors( containerColor = Color.Transparent )
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        color = Color.White
                    )
                    Text(
                        text = "Inicia sesión.",
                        color = Variables.Color4
                    )
                }

                Spacer(Modifier.height(30.dp))

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Variables.Color4,
                        contentColor = Variables.Color6
                    ),
                    modifier = Modifier.width(300.dp),
                ) {
                    Text("Crear cuenta", style=styles.smallText)
                }
            }
        }
    }
}