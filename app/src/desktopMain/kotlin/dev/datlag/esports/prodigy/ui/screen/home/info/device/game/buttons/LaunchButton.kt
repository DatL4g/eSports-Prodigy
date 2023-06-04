package dev.datlag.esports.prodigy.ui.screen.home.info.device.game.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.common.openInBrowser
import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.ui.theme.LeftRoundedShape
import dev.datlag.esports.prodigy.ui.theme.RightRoundedShape
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun RowScope.LaunchButton(game: Game) {
    val startError = stringResource(SharedRes.strings.could_not_start_game)

    when (game) {
        is Game.Steam -> {
            SingleGameLaunchButton {
                val appId = game.manifest.appId
                var result = "steam://rungameid/$appId".openInBrowser(startError)
                if (result.isFailure) {
                    result = "steam://run/$appId".openInBrowser(startError)
                }
                result.isFailure
            }
        }

        is Game.Heroic -> {
            SingleGameLaunchButton {
                val launchId = game.app.appName ?: game.app.title
                val result = "heroic://launch/$launchId".openInBrowser(startError)
                result.isFailure
            }
        }

        is Game.Multi -> {
            MultiGameLaunchButton(game) {
                it to true
            }
        }
    }
}

@Composable
private fun SingleGameLaunchButton(launching: () -> Boolean) {
    var launchClickable by remember { mutableStateOf(true) }

    Button(
        onClick = {
            if (launchClickable) {
                launchClickable = false
                launchClickable = launching()
            }
        },
        enabled = launchClickable
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = stringResource(SharedRes.strings.play)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = "Launch",
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MultiGameLaunchButton(game: Game.Multi, launching: (Game.TYPE) -> Pair<Game.TYPE, Boolean>) {
    var launchClickable by remember { mutableStateOf(true) }

    val preferredGame = game.steam ?: game.heroic

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                if (launchClickable) {
                    launchClickable = false
                }
            },
            enabled = launchClickable,
            shape = LeftRoundedShape
        ) {
            val painter = when (preferredGame) {
                is Game.Steam -> painterResource(SharedRes.images.steam)
                is Game.Heroic -> painterResource(SharedRes.images.rocket_league)
                else -> null
            }
            if (painter != null) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painter,
                    contentDescription = stringResource(SharedRes.strings.play)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(SharedRes.strings.play)
                )
            }
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = "Launch",
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = {

            },
            shape = RightRoundedShape
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "more"
            )
        }
    }
}