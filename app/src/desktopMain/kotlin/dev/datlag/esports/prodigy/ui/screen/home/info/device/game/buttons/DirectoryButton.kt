package dev.datlag.esports.prodigy.ui.screen.home.info.device.game.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.model.common.scopeCatching
import dev.datlag.esports.prodigy.ui.theme.LeftRoundedShape
import dev.datlag.esports.prodigy.ui.theme.RightRoundedShape
import java.awt.Desktop
import java.io.File


@Composable
fun RowScope.DirectoryButton(game: Game) {
    val openSupported = scopeCatching {
        Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)
    }.getOrNull() ?: false

    when (game) {
        is Game.Steam -> {
            SingleDirectoryButton(openSupported, game.directories.firstNotNullOfOrNull { it.value })
        }
        is Game.Heroic -> {
            SingleDirectoryButton(openSupported, game.directories.firstNotNullOfOrNull { it.value })
        }
        is Game.Multi -> {
            MultiDirectoryButton(openSupported, game)
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
        enabled = openSupported && directory != null,
        border = BorderStroke(
            width = ButtonDefaults.outlinedButtonBorder.width,
            color = if (openSupported) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12F)
        )
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
private fun RowScope.MultiDirectoryButton(openSupported: Boolean, game: Game.Multi) {
    val preferredGame = game.steam ?: game.heroic
    val preferredDirectory = preferredGame?.directories?.firstNotNullOfOrNull { it.value }
    val borderStroke = BorderStroke(
        width = ButtonDefaults.outlinedButtonBorder.width,
        color = if (openSupported) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12F)
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {
                scopeCatching {
                    Desktop.getDesktop().open(preferredDirectory)
                }
            },
            enabled = openSupported && preferredDirectory != null,
            shape = LeftRoundedShape,
            border = borderStroke
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

            },
            border = borderStroke,
            shape = RightRoundedShape
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = "more"
            )
        }
    }
}