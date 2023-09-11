package dev.datlag.esports.prodigy.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.datlag.esports.prodigy.common.getValueBlocking
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings
import dev.datlag.esports.prodigy.game.SteamLauncher
import dev.datlag.esports.prodigy.model.ThemeMode
import dev.datlag.esports.prodigy.model.common.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.apache.commons.lang3.SystemUtils
import java.io.File

@Composable
actual fun SettingsScreen(component: SettingsComponent) {
    val steamDirs by SteamLauncher.steamFolders.collectAsStateWithLifecycle(initialValue = emptyList())
    val dialogState by component.dialog.subscribeAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        item {
            Button(
                onClick = {
                    component.back()
                }
            ) {
                Text(text = "Back")
            }
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Steam",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1F))
                Button(
                    onClick = {
                        component.showDialog(DialogConfig.SteamFinder)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Search")
                }
            }
        }
        items(steamDirs) {
            Card(modifier = Modifier.padding(vertical = 8.dp).fillParentMaxWidth()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = it.absolutePath
                )
            }
        }
        item {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            val themeMode by component.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            var showMenu by remember { mutableStateOf(false) }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Theming"
                )
                Spacer(modifier = Modifier.weight(1F))
                Button(
                    onClick = {
                        showMenu = !showMenu
                    }
                ) {
                    Text(
                        text = when (themeMode) {
                            is ThemeMode.LIGHT -> "Light"
                            is ThemeMode.DARK -> "Dark"
                            else -> "System"
                        }
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "More"
                    )

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                component.changeThemeMode(ThemeMode.LIGHT)
                            },
                            enabled = themeMode != ThemeMode.LIGHT,
                            text = {
                                Text(
                                    text = "Light Theme"
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LightMode,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                component.changeThemeMode(ThemeMode.DARK)
                            },
                            enabled = themeMode != ThemeMode.DARK,
                            text = {
                                Text(
                                    text = "Dark Theme"
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                component.changeThemeMode(ThemeMode.SYSTEM)
                            },
                            enabled = themeMode != ThemeMode.SYSTEM,
                            text = {
                                Text(
                                    text = "System Theme"
                                )
                            },
                            leadingIcon = {
                                val icon = when {
                                    SystemUtils.IS_OS_WINDOWS -> Icons.Default.DesktopWindows
                                    SystemUtils.IS_OS_MAC -> Icons.Default.DesktopMac
                                    else -> Icons.Default.Computer
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val contentColors by component.contentColors.collectAsStateWithLifecycle(initialValue = true)

                Text(
                    text = "Use Content Colors"
                )
                Spacer(modifier = Modifier.weight(1F))
                Switch(
                    checked = contentColors,
                    onCheckedChange = {
                        component.changeContentColors(it)
                    }
                )
            }
        }
    }

    dialogState.child?.also { (_, instance) ->
        instance.render()
    }
}