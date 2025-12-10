package com.e243768.organipro_.presentation.views.profile.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.e243768.organipro_.R

@Composable
fun ProfileAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit // <--- Callback para abrir la galería
) {
    Box(modifier = modifier) {
        // Tu imagen (AsyncImage o Image)
        AsyncImage(
            model = imageUrl ?: R.drawable.placeholder_user,
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable { onEditClick() } // <--- Clickable aquí
        )

        // Icono de camarita opcional encima
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Cambiar foto",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(Color.Black.copy(alpha=0.6f), CircleShape)
                .padding(8.dp)
                .size(16.dp)
        )
    }
}