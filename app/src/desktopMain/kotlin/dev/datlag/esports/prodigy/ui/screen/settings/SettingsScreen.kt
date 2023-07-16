package dev.datlag.esports.prodigy.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.getValueBlocking
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.game.SteamLauncher
import dev.datlag.esports.prodigy.model.ThemeMode
import dev.datlag.esports.prodigy.model.common.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.apache.commons.lang3.SystemUtils
import java.io.File

@Composable
actual fun SettingsScreen(component: SettingsComponent) {
    val steamDirs by SteamLauncher.steamFolders.collectAsStateSafe { emptyList() }
    val dialogState by component.dialog.subscribeAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
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
                    style = MaterialTheme.typography.titleMedium,
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
                    Text("Find")
                }
            }
        }
        items(steamDirs) {
            ElevatedCard(modifier = Modifier.fillParentMaxWidth()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = it.absolutePath
                )
            }
        }
        item {
            val themeMode by component.themeMode.collectAsStateSafe {
                ThemeMode.SYSTEM
            }
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
    }

    dialogState.child?.also { (_, instance) ->
        instance.render()
    }
}