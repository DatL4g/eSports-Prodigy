package dev.datlag.esports.prodigy.ui.screen.home.info.device.game.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
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
                launchSteam(startError, game)
            }
        }

        is Game.Heroic -> {
            SingleGameLaunchButton {
                launchHeroic(startError, game)
            }
        }

        is Game.Multi -> {
            MultiGameLaunchButton(game) { launchGame ->
                when (launchGame) {
                    is Game.Steam -> {
                        launchSteam(startError, launchGame)
                    }
                    is Game.Heroic -> {
                        launchHeroic(startError, launchGame)
                    }
                    else -> true
                }
            }
        }
    }
}

private fun launchSteam(startError: String, game: Game.Steam): Boolean {
    val appId = game.manifest.appId
    var result = "steam://rungameid/$appId".openInBrowser(startError)
    if (result.isFailure) {
        result = "steam://run/$appId".openInBrowser(startError)
    }
    return result.isFailure
}

private fun launchHeroic(startError: String, game: Game.Heroic): Boolean {
    val launchId = game.app.appName ?: game.app.title
    val result = "heroic://launch/$launchId".openInBrowser(startError)
    return result.isFailure
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
private fun MultiGameLaunchButton(game: Game.Multi, launching: (Game) -> Boolean) {
    var launchClickable by remember { mutableStateOf(true) }
    val preferredGame = game.steam ?: game.heroic
    var showMenu by remember { mutableStateOf(false) }

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
                showMenu = true
            },
            shape = RightRoundedShape
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "more"
            )
        }
        LaunchDropDown(
            expanded = showMenu,
            game = game,
            ignoreType = preferredGame?.type,
            launching = { launching(it) },
        ) {
            showMenu = false
        }
    }
}

@Composable
private fun LaunchDropDown(
    expanded: Boolean,
    game: Game.Multi,
    ignoreType: Game.TYPE?,
    launching: (Game) -> Boolean,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            onDismiss()
        }
    ) {
        game.games.forEach {
            if (it.type != ignoreType && it.type != null) {
                val (painter, prefix) = when (it.type!!) {
                    is Game.TYPE.STEAM -> painterResource(SharedRes.images.steam) to "Steam"
                    is Game.TYPE.HEROIC -> painterResource(SharedRes.images.rocket_league) to "Heroic"
                }

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painter,
                                contentDescription = prefix
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("$prefix Launch")
                        }
                    },
                    onClick = {
                        launching(it)
                        onDismiss()
                    },
                    enabled = it.type != null
                )
            }
        }
    }
}