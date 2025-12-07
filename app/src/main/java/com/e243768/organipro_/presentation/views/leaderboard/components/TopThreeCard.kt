package com.e243768.organipro_.presentation.views.leaderboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.e243768.organipro_.presentation.viewmodels.leaderboard.LeaderboardUser

@Composable
fun TopThreeCard(
    users: List<LeaderboardUser>,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFC700),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        users.take(3).forEach { user ->
            TopThreeItem(
                user = user,
                onClick = { onUserClick(user.id) }
            )

            if (user.rank < 3) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun TopThreeItem(
    user: LeaderboardUser,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF2A214D),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Medalla/Icono de posición
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    color = when (user.rank) {
                        1 -> Color(0xFFFFC700) // Oro
                        2 -> Color(0xFFE0E0E0) // Plata
                        3 -> Color(0xFFCD7F32) // Bronce
                        else -> Color(0xFF8A5CFF)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (user.rank <= 3) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Medalla",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = user.rank.toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Nombre de usuario
        Text(
            text = user.name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // Puntos
        Text(
            text = user.points,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}