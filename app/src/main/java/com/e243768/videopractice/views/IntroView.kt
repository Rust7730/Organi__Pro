package com.e243768.videopractice.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import GradientWithStarsBackground
import com.e243768.videopractice.R
import com.e243768.videopractice.fonts.fontStyles
import com.e243768.videopractice.navigation.Routes

@Composable
fun vista (navController: NavController, styles: fontStyles){

    Box(modifier = Modifier.fillMaxSize()) {
        GradientWithStarsBackground()
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lista),
                    contentDescription = "Not Found Image",
                    modifier = Modifier.size(400.dp)
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    "Organiza tus tareas \n" +
                            "Con OrganiPro",
                    style = styles.smallHighlightedText,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.padding(15.dp))
                Button(
                    {
                        navController.navigate(Routes.Login)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7A5EFF),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.width(300.dp),
                ) {
                    Text("Siguiente", style=styles.smallText)
                }
            }
        }
    }
}