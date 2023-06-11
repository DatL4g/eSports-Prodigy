package dev.datlag.esports.prodigy.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.getValueBlocking
import dev.datlag.esports.prodigy.model.ThemeMode

@Composable
actual fun SettingsScreen(component: SettingsComponent) {
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
            val themeMode by component.themeMode.collectAsStateSafe {
                component.themeMode.getValueBlocking(ThemeMode.SYSTEM)
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
                        val composer = currentComposer

                        if (themeMode !is ThemeMode.LIGHT) {
                            DropdownMenuItem(
                                onClick = {
                                    component.changeThemeMode(ThemeMode.LIGHT)
                                },
                                enabled = true,
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
                        }
                        if (themeMode !is ThemeMode.DARK) {
                            DropdownMenuItem(
                                onClick = {
                                    component.changeThemeMode(ThemeMode.DARK)
                                },
                                enabled = true,
                                text = {
                                    Text(
                                        text = "Dark Theme"
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LightMode,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                        if (themeMode !is ThemeMode.SYSTEM) {
                            DropdownMenuItem(
                                onClick = {
                                    component.changeThemeMode(ThemeMode.SYSTEM)
                                },
                                enabled = true,
                                text = {
                                    Text(
                                        text = "System Theme"
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LightMode,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}