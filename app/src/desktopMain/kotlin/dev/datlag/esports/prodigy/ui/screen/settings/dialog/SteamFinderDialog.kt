package dev.datlag.esports.prodigy.ui.screen.settings.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.toSize
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle
import dev.datlag.esports.prodigy.common.toDpSize
import dev.datlag.esports.prodigy.model.common.isSame
import java.io.File
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SteamFinderDialog(component: SteamFinderComponent) {
    val searchState by component.searchState.collectAsStateWithLifecycle(initialValue = null)

    AlertDialog(
        onDismissRequest = {
            component.dismiss()
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Find Steam"
            )
        },
        title = {
            Text(
                text = "Find Steam",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                val entryWidth = remember { mutableStateOf(0) }

                Text(text = "This tries to find all your Steam installations.\nIt may take a lot of time.")
                when (searchState) {
                    null -> { }
                    is SteamFinderComponent.State.RUNNING -> {
                        val currentSearchDir by component.currentSearchDir.collectAsStateWithLifecycle(initialValue = null)

                        currentSearchDir?.let {
                            Text(
                                modifier = Modifier.padding(vertical = 16.dp).sizeIn(
                                    maxWidth = max(with(LocalDensity.current) {
                                        entryWidth.value.toDp()
                                    }, 300.dp)
                                ),
                                text = it.absolutePath,
                                overflow = TextOverflow.Ellipsis,
                                softWrap = true,
                                maxLines = 2
                            )
                        }
                    }
                    is SteamFinderComponent.State.FINISHED -> {
                        Text(
                            modifier = Modifier.padding(vertical = 16.dp),
                            text = "Finished search, below are the found results.",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is SteamFinderComponent.State.EXISTING -> {
                        Text(
                            modifier = Modifier.padding(vertical = 16.dp),
                            text = "Finished search, all results are already managed.",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    val unmanagedItems by component.unmanagedSteamDirs.collectAsStateWithLifecycle(initialValue = emptyList())
                    val managedItems by component.managedSteamDirs.collectAsStateWithLifecycle(initialValue = emptyList())
                    val settingsItems by component.settingsExisting.collectAsStateWithLifecycle(initialValue = emptyList())

                    managedItems.forEach {
                        ItemCard(
                            item = it,
                            managed = true,
                            entryWidth = entryWidth,
                            deletable = searchState !is SteamFinderComponent.State.RUNNING
                        ) {
                            component.deleteItem(it)
                        }
                    }
                    unmanagedItems.forEach {
                        ItemCard(
                            item = it,
                            managed = false,
                            saved = settingsItems.any { e -> e.isSame(it) },
                            entryWidth = entryWidth,
                            deletable = searchState !is SteamFinderComponent.State.RUNNING
                        ) {
                            component.deleteItem(it)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (searchState) {
                        is SteamFinderComponent.State.FINISHED -> {
                            component.save()
                        }
                        is SteamFinderComponent.State.EXISTING -> {
                            component.dismiss()
                        }
                        else -> component.search()
                    }
                },
                enabled = searchState !is SteamFinderComponent.State.RUNNING
            ) {
                val buttonText = remember(searchState) { when (searchState) {
                    is SteamFinderComponent.State.FINISHED -> "Save"
                    is SteamFinderComponent.State.EXISTING -> "Close"
                    else -> "Search"
                } }
                Text(text = buttonText)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    component.dismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError)
            ) {
                Text(text = "Dismiss")
            }
        }
    )
}

@Composable
private fun ItemCard(
    item: File,
    managed: Boolean,
    saved: Boolean = managed,
    entryWidth: MutableState<Int>,
    deletable: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth().onSizeChanged {
            entryWidth.value = max(entryWidth.value, it.width)
        }
    ) {
        var iconSize by remember { mutableStateOf(Size.Unspecified) }

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (managed || saved) {
                Icon(
                    modifier = Modifier.onSizeChanged {
                        iconSize = it.toSize()
                    },
                    imageVector = Icons.Default.Folder,
                    contentDescription = "Exists"
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CreateNewFolder,
                    contentDescription = "New"
                )
            }
            Text(
                text = item.absolutePath
            )
            if (!managed) {
                IconButton(
                    modifier = Modifier.size(iconSize.toDpSize(24.dp, 24.dp)),
                    onClick = {
                        onDelete()
                    },
                    enabled = deletable
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}