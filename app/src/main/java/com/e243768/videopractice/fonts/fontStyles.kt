package com.e243768.videopractice.fonts

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.e243768.videopractice.R
import com.e243768.videopractice.variables.Variables

class fontStyles(){
    val headerStyle: TextStyle = TextStyle(
        fontSize = 32.sp,
        fontFamily = FontFamily(Font(R.font.montserrat_bold)),
        fontWeight = FontWeight(600),
        color = Variables.Color6,
        textAlign = TextAlign.Center
    )

    val smallText: TextStyle = TextStyle( fontSize = 24.sp, color = Variables.Color6 )
    val smallHighlightedText: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontWeight = Bold,
        color = Variables.Color6,
        textAlign = TextAlign.Center
    )
}