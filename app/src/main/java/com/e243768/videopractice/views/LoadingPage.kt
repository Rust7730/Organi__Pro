package com.e243768.videopractice.views

import GradientWithStarsBackground
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataSaverOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.e243768.videopractice.R
import com.e243768.videopractice.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun loading (navController: NavController){
    LaunchedEffect(key1 = true) {
        delay(500)
        navController.navigate(Routes.Intro)
    }
    Box(modifier=Modifier.fillMaxSize()){
        GradientWithStarsBackground()

        Scaffold (

            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()){ innerPadding ->
            Column(modifier = Modifier.
            fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(R.drawable.noimage),
                    contentDescription = "notfoundimage",
                    modifier = Modifier.width(200.dp))
                Spacer(modifier = Modifier.padding(50.dp))
                Icon(imageVector = Icons.Filled.DataSaverOff , contentDescription ="load",
                    modifier = Modifier.width(100.dp).height(100.dp),
                    tint = Color(0xFF7A5EFF))


                
            }
        }

    }
}