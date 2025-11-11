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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.e243768.videopractice.R
import com.e243768.videopractice.variables.Variables

@Composable
fun login(){
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
            style = TextStyle(
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.montserrat_bold)),
                fontWeight = FontWeight(600),
                color = Variables.Color6,
                textAlign = TextAlign.Center,

            )
        )

            Column(
                modifier = Modifier
                    .padding(start = 30.dp, top = 50.dp, end = 30.dp, bottom = 50.dp)
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


                verticalArrangement = Arrangement.spacedBy(50.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

            }
        }
    }
    }
}
