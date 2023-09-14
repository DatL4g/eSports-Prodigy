package dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.game.model.LocalGame
import dev.datlag.esports.prodigy.game.model.LocalGameInfo
import dev.datlag.esports.prodigy.model.common.homeDirectory
import dev.datlag.esports.prodigy.model.common.move
import dev.icerock.moko.resources.compose.painterResource
import java.io.File
import kotlin.math.max


@Composable
fun CacheCard(
    game: LocalGame,
    gameInfo: LocalGameInfo?,
    type: LocalGameInfo.TYPE,
    cache: DxvkStateCache,
    width: Int,
    height: Int,
    onSizeChange: (Pair<Int, Int>) -> Unit
) {
    val widthDp = with(LocalDensity.current) {
        width.toDp()
    }
    val heightDp = with(LocalDensity.current) {
        height.toDp()
    }
    val scope = rememberCoroutineScope()

    fun Modifier.sameSize() = when {
        width > 0 && height > 0 -> then(Modifier.defaultMinSize(
            minWidth = widthDp,
            minHeight = heightDp
        ))
        width > 0 -> then(Modifier.defaultMinSize(
            minWidth = widthDp
        ))
        height > 0 -> then(Modifier.defaultMinSize(
            minHeight = heightDp
        ))
        else -> then(Modifier)
    }

    var showExportPicker by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier.padding(vertical = 2.dp).sameSize().onSizeChanged {
            if (width < it.width || height > it.height) {
                onSizeChange(
                    max(width, it.width) to max(height, it.height)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            val (painter, name) = when (type) {
                is LocalGameInfo.TYPE.STEAM -> painterResource(SharedRes.images.steam) to "Steam"
                is LocalGameInfo.TYPE.HEROIC -> painterResource(SharedRes.images.heroic) to "Heroic"
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painter,
                    contentDescription = name
                )
                Text(
                    text = cache.file.nameWithoutExtension,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier.padding(vertical = 8.dp)
                    .width(widthDp - 32.dp)
                    .height(DividerDefaults.Thickness)
                    .background(DividerDefaults.color)
            )
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Version:",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Entries:",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Invalid:",
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = cache.header.version.toString())
                    Text(text = cache.totalEntries.toString())
                    Text(text = cache.invalidEntries.toString())
                }
            }
            if (cache.invalidEntries > 0) {
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        scope.launchIO { repairCache(game, gameInfo, cache) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(
                        text = "Repair",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        showExportPicker = true
                    }
                ) {
                    Text(text = "Export")
                }
            }
        }
        DirectoryPicker(showExportPicker, homeDirectory()?.canonicalPath) { path ->
            showExportPicker = false

            if (path?.ifEmpty { null } != null) {
                scope.launchIO {
                    exportCache(path, cache)
                }
            }
        }
    }
}

private suspend fun repairCache(game: LocalGame, gameInfo: LocalGameInfo?, cache: DxvkStateCache) {
    val originalName = cache.file.name
    val backupFile = cache.file.move("$originalName.bak")

    val loadBackupFile = cache.writeToFile(cache.file).isFailure
    if (loadBackupFile) {
        backupFile.move(originalName)
    }

    gameInfo?.reloadDxvkCaches() ?: game.reloadDxvkCaches()
}

private suspend fun exportCache(path: String, cache: DxvkStateCache) {
    cache.writeToFile(File(path, cache.file.name))
}