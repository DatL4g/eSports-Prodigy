package dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HeroBackButton(onClick: () -> Unit) {
    when (calculateWindowSizeClass().widthSizeClass) {
        WindowWidthSizeClass.Compact -> CompactBackButton(onClick)
        WindowWidthSizeClass.Medium -> MediumBackButton(onClick)
        WindowWidthSizeClass.Expanded -> MediumBackButton(onClick)
    }
}

@Composable
private fun CompactBackButton(onClick: () -> Unit) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = Modifier.padding(8.dp).background(
            color = Color.Black.copy(alpha = 0.5F),
            shape = CircleShape
        )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
        )
    }
}

@Composable
private fun MediumBackButton(onClick: () -> Unit) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = Modifier.background(
            color = Color.Black.copy(alpha = 0.5F),
            shape = RoundedCornerShape(
                bottomEnd = 16.dp
            )
        )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White
        )
    }
}