package com.e243768.organipro_

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.e243768.organipro_.presentation.navigation.NavManager
import com.e243768.organipro_.ui.theme.OrganiPro_Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OrganiPro_Theme(darkTheme = true) {
                NavManager()
            }
        }
    }
}