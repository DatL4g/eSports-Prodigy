package dev.datlag.esports.prodigy.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.datastore.core.DataStore
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.getValueBlocking
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings
import dev.datlag.esports.prodigy.model.ThemeMode
import dev.datlag.esports.prodigy.ui.theme.Colors
import dev.datlag.esports.prodigy.ui.theme.*
import kotlinx.coroutines.flow.map
import org.kodein.di.DI
import org.kodein.di.instance

@Composable
fun App(
    di: DI,
    systemDarkTheme: Boolean = isSystemInDarkTheme() || getSystemDarkMode(),
    content: @Composable () -> Unit
) {
    val settings: DataStore<AppSettings> by di.instance()

    val themeMode by settings.data.map { ThemeMode.ofValue(it.appearance.themeMode) }.collectAsStateSafe {
        ThemeMode.SYSTEM
    }
    val useDarkTheme = when (themeMode) {
        is ThemeMode.LIGHT -> false
        is ThemeMode.DARK -> true
        else -> systemDarkTheme
    }

    CompositionLocalProvider(LocalDarkMode provides useDarkTheme) {
        MaterialTheme(
            colorScheme = if (useDarkTheme) Colors.getDarkScheme() else Colors.getLightScheme(),
            typography = ManropeTypography()
        ) {
            androidx.compose.material.MaterialTheme(
                colors = MaterialTheme.colorScheme.toLegacyColors(useDarkTheme),
                shapes = MaterialTheme.shapes.toLegacyShapes(),
                typography = ManropeTypographyLegacy()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
expect fun getSystemDarkMode(): Boolean

@Composable
expect fun loadImageScheme(key: Any, painter: Painter)