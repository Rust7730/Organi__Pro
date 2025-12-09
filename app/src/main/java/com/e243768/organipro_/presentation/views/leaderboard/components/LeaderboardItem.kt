package com.e243768.organipro_.presentation.views.leaderboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun LeaderboardItem(
    user: LeaderboardUser,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (user.isCurrentUser) {
                    Color(0xFF8A5CFF).copy(alpha = 0.3f)
                } else {
                    Color(0xFF2A214D)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (user.isCurrentUser) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFF8A5CFF),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número de ranking
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color = Color(0xFF1A1335)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${user.rank}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Nombre de usuario
        Text(
            text = user.name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = if (user.isCurrentUser) FontWeight.Bold else FontWeight.Medium,
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