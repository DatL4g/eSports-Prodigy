package dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.game.model.LocalGame
import dev.datlag.esports.prodigy.game.model.LocalGameInfo
import dev.datlag.esports.prodigy.model.common.scopeCatching
import dev.datlag.esports.prodigy.ui.theme.LeftRoundedShape
import dev.datlag.esports.prodigy.ui.theme.RightRoundedShape
import dev.icerock.moko.resources.compose.painterResource
import java.awt.Desktop
import java.io.File


@Composable
fun RowScope.DirectoryButton(game: LocalGame) {
    val openSupported = scopeCatching {
        Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)
    }.getOrNull() ?: false

    when {
        game.steam != null && game.heroic != null -> {
            MultiDirectoryButton(openSupported, game)
        }
        game.steam != null -> {
            SingleDirectoryButton(openSupported, game.steamDirectory)
        }
        game.heroic != null -> {
            SingleDirectoryButton(openSupported, game.heroicDirectory)
        }
    }
}

@Composable
private fun RowScope.SingleDirectoryButton(openSupported: Boolean, directory: File?) {
    OutlinedButton(
        onClick = {
            scopeCatching {
                Desktop.getDesktop().open(directory)
            }
        },
        enabled = openSupported && directory != null
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = "Location",
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.MultiDirectoryButton(openSupported: Boolean, game: LocalGame) {
    val (type, preferredDirectory) = game.steamDirectory?.let {
        LocalGameInfo.TYPE.STEAM to it
    } ?: game.heroicDirectory?.let {
        LocalGameInfo.TYPE.HEROIC to it
    } ?: (null to null)
    var showMenu by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {
                scopeCatching {
                    Desktop.getDesktop().open(preferredDirectory)
                }.getOrNull()
            },
            enabled = openSupported && preferredDirectory != null,
            shape = LeftRoundedShape
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = "Location"
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = "Location",
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
        OutlinedButton(
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
        DirectoryDropDown(
            showMenu,
            game,
            type,
            openSupported
        ) {
            showMenu = false
        }
    }
}

@Composable
private fun DirectoryDropDown(
    expanded: Boolean,
    game: LocalGame,
    ignoreType: LocalGameInfo.TYPE?,
    openSupported: Boolean,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            onDismiss()
        }
    ) {
        game.directories.forEach { (t, u) ->
            if (t != ignoreType) {
                val (painter, prefix) = when (t) {
                    is LocalGameInfo.TYPE.STEAM -> painterResource(SharedRes.images.steam) to "Steam"
                    is LocalGameInfo.TYPE.HEROIC -> painterResource(SharedRes.images.heroic) to "Heroic"
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
                            Text("$prefix Location")
                        }
                    },
                    onClick = {
                        scopeCatching {
                            Desktop.getDesktop().open(u)
                        }
                        onDismiss()
                    },
                    enabled = openSupported && u != null
                )
            }
        }
    }
}