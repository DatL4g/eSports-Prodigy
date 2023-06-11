package dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.game.model.LocalGame
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun RowScope.GameLauncherIcons(game: LocalGame) {
    when {
        game.steam != null && game.heroic != null -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SteamIcon()
                HeroicIcon()
            }
        }
        game.steam != null -> {
            SteamIcon()
        }
        game.heroic != null -> {
            HeroicIcon()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SteamIcon() {
    TooltipArea(
        tooltip = {
            Text(
                modifier = Modifier.clip(
                    RoundedCornerShape(4.dp)
                ).background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp),
                text = "Steam",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(SharedRes.images.steam),
            contentDescription = "Steam"
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeroicIcon() {
    TooltipArea(
        tooltip = {
            Text(
                modifier = Modifier.clip(
                    RoundedCornerShape(4.dp)
                ).background(MaterialTheme.colorScheme.surfaceVariant).padding(4.dp),
                text = "Heroic",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(SharedRes.images.heroic),
            contentDescription = "Heroic"
        )
    }
}