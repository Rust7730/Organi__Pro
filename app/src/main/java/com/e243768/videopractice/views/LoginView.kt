package com.e243768.videopractice.views

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import GradientWithStarsBackground
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.e243768.videopractice.R
import com.e243768.videopractice.variables.Variables
import com.e243768.videopractice.fonts.fontStyles
import com.e243768.videopractice.styles.componentStyles

@Composable
fun login(navController: NavController, styles: fontStyles, componentStyles: componentStyles){
    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }

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
                text = "¡Bienvenido de\nvuelta!",
                style = styles.headerStyle
                )

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
                        .height(298.dp)
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(size = 20.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
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
                        value = contraseña,
                        onValueChange = { contraseña = it },
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
                    onClick = {},
                    colors = ButtonDefaults.buttonColors( containerColor = Color.Transparent )
                ) {
                    Text("No tienes cuenta? ")
                    Text("Crea una cuenta.")
                }
            }
        }
    }
}
