package com.e243768.organipro_.presentation.views.profile.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProfileAvatar(
    avatarResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(150.dp)
            .clip(CircleShape)
            .border(
                width = 4.dp,
                color = Color(0xFF8A5CFF),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder circular con gradiente
        Canvas(modifier = Modifier.size(142.dp)) {
            drawCircle(color = Color(0xFF8A5CFF))
        }

        // TODO: Cargar imagen real cuando esté disponible
        // if (avatarResId != 0) {
        //     Image(
        //         painter = painterResource(id = avatarResId),
        //         contentDescription = "Avatar",
        //         modifier = Modifier.fillMaxSize(),
        //         contentScale = ContentScale.Crop
        //     )
        // }
    }
}