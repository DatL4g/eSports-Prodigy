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
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.common.ifTrue
import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.game.model.Game
import dev.icerock.moko.resources.compose.painterResource
import kotlin.math.max


@Composable
fun CacheCard(
    type: Game.TYPE,
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

    fun Modifier.sameSize() = when {
        width > 0 && height > 0 -> then(Modifier.size(
            width = widthDp,
            height = heightDp
        ))
        width > 0 -> then(Modifier.width(
            width = widthDp
        ))
        height > 0 -> then(Modifier.height(
            height = heightDp
        ))
        else -> then(Modifier)
    }

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
                is Game.TYPE.STEAM -> painterResource(SharedRes.images.steam) to "Steam"
                is Game.TYPE.HEROIC -> painterResource(SharedRes.images.heroic) to "Heroic"
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

                    }
                ) {
                    Text(text = "Export")
                }
            }
        }
    }
}