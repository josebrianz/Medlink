package com.example.medilink2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink2.data.NotificationLogicManager
import com.example.medilink2.data.UserManager

@Composable
fun NotificationBadge(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = Color.White,
    badgeColor: Color = Color.Red,
) {
    val userId = UserManager.getUserId()
    val unreadCount by (if (userId != null) {
        NotificationLogicManager.getUnreadNotificationsCount(userId)
    } else {
        kotlinx.coroutines.flow.flowOf(0)
    }).collectAsState(initial = 0)

    Box(modifier = modifier.size(48.dp)) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Icon(
                Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = iconColor,
            )
        }

        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .size(18.dp)
                    .background(badgeColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = unreadCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
