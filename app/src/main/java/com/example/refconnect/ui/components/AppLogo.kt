package com.example.refconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: LogoSize = LogoSize.MEDIUM
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo icon in circle
        Box(
            modifier = Modifier
                .size(size.iconSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = "RefConnect Logo",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(size.iconSize * 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(size.spacing))

        // App name
        Text(
            text = "RefConnect",
            fontSize = size.textSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        if (size.showTagline) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your Professional Referral Network",
                fontSize = (size.textSize.value * 0.35f).sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

enum class LogoSize(
    val iconSize: androidx.compose.ui.unit.Dp,
    val textSize: androidx.compose.ui.unit.TextUnit,
    val spacing: androidx.compose.ui.unit.Dp,
    val showTagline: Boolean
) {
    SMALL(48.dp, 20.sp, 8.dp, false),
    MEDIUM(80.dp, 32.sp, 12.dp, true),
    LARGE(120.dp, 48.sp, 16.dp, true)
}