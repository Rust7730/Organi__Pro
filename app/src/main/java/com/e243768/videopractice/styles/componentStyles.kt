package com.e243768.videopractice.styles

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.e243768.videopractice.variables.Variables

class componentStyles() {
    /*
    val compStyles = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent
        )
     */
    val textFieldModifier: Modifier = Modifier
        .border(
            width = 2.dp,
            shape = RoundedCornerShape(size = 40.dp),
            color = Variables.Color4
        )
}
