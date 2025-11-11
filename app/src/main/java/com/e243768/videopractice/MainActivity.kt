package com.e243768.videopractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.e243768.videopractice.ui.theme.VideoPracticeTheme
import com.e243768.videopractice.navigation.vistas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            VideoPracticeTheme(darkTheme = true) {
                vistas()
            }
        }
    }
}