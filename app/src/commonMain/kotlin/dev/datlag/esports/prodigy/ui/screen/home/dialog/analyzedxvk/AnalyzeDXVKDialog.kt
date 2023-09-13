package dev.datlag.esports.prodigy.ui.screen.home.dialog.analyzedxvk

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.common.onClick
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import dev.datlag.esports.prodigy.color.utils.ThemeUtils
import dev.datlag.esports.prodigy.common.DragDrop
import dev.datlag.esports.prodigy.common.blend
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle
import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.model.common.canReadSafely
import dev.datlag.esports.prodigy.model.common.homeDirectory
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import java.io.File

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalyzeDXVKDialog(component: AnalyzeDXVKComponent) {
    AlertDialog(
        onDismissRequest = {
            component.dismiss()
        },
        icon = {
            Icon(
                imageVector = Icons.Default.DataUsage,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = "Analyze DXVK State-Cache",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val caches by component.dxvkStateCaches.collectAsStateWithLifecycle(emptyList())

                DragDropArea(component)
                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    caches.forEach {
                        CacheCard(it)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    component.dismiss()
                }
            ) {
                Text(text = "Close")
            }
        }
    )
}

@Composable
private fun DragDropArea(component: AnalyzeDXVKComponent) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.secondary)
    ) {
        var showPicker by remember { mutableStateOf(false) }

        Box(
            Modifier.fillMaxWidth().padding(32.dp).onClick {
                showPicker = !showPicker
            },
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Click to select a cache file or just drag-n-drop it here.",
                textAlign = TextAlign.Center
            )
        }
        FilePicker(
            show = showPicker,
            initialDirectory = homeDirectory()?.absolutePath,
            fileExtensions = listOf("dxvk-cache")
        ) { file ->
            showPicker = false

            component.analyzeFiles(file?.platformFile as? File)
        }

        DragDrop(Unit, predicate = {
            it.canReadSafely()
        }) { list ->
            component.analyzeFiles(list)
        }
    }
}

@Composable
private fun CacheCard(cache: DxvkStateCache) {
    val isInvalid = cache.invalidEntries > 0

    SchemeTheme(key = if (isInvalid) SchemeTheme.COLOR_KEY.ERROR else null, ignoreSettings = true) {
        OutlinedCard {
            Text(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                text = cache.file.name,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.padding(8.dp),
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
        }
    }
}