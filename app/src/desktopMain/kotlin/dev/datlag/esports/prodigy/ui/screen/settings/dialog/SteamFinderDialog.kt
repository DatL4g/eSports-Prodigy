package dev.datlag.esports.prodigy.ui.screen.settings.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.model.common.isSame
import kotlin.math.max

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SteamFinderDialog(component: SteamFinderComponent) {
    val searchState by component.searchState.collectAsStateSafe { null }

    AlertDialog(
        onDismissRequest = {
            component.dismiss()
        },
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Find Steam"
                )
                Text(
                    text = "Find Steam",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Column {
                var entryWidth by remember { mutableStateOf(0) }

                Text(text = "This tries to find all your Steam installations.\nIt may take a lot of time.")
                when (searchState) {
                    null -> { }
                    is SteamFinderComponent.State.RUNNING -> {
                        val currentSearchDir by component.currentSearchDir.collectAsStateSafe { null }

                        currentSearchDir?.let {
                            Text(
                                modifier = Modifier.padding(vertical = 16.dp).sizeIn(
                                    maxWidth = max(with(LocalDensity.current) {
                                        entryWidth.toDp()
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
                    val items by component.foundSteamDirs.collectAsStateSafe { emptyList() }
                    val existingItems by component.existingSteamDirs.collectAsStateSafe { emptyList() }

                    items.forEach {
                        Card(
                            modifier = Modifier.fillMaxWidth().onSizeChanged {
                                entryWidth = max(entryWidth, it.width)
                            }
                        ) {
                            if (existingItems.any { e -> e.isSame(it) }) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = "Exists"
                                    )
                                    Text(
                                        text = it.absolutePath
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CreateNewFolder,
                                        contentDescription = "New"
                                    )
                                    Text(
                                        text = it.absolutePath
                                    )
                                }
                            }
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
                            // ToDo("save unmanaged results")
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