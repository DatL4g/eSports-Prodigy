package dev.datlag.esports.prodigy.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.datastore.core.DataStore
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.getValueBlocking
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings
import dev.datlag.esports.prodigy.game.Celebrity
import dev.datlag.esports.prodigy.model.ThemeMode
import dev.datlag.esports.prodigy.ui.theme.Colors
import dev.datlag.esports.prodigy.ui.theme.*
import kotlinx.coroutines.flow.map
import org.kodein.di.DI
import org.kodein.di.instance
import dev.datlag.esports.prodigy.other.Commonizer

val LocalCommonizer = compositionLocalOf<Commonizer> { error("No Commonizer state provided") }
val LocalCelebrity = compositionLocalOf<Celebrity?> { null }
val LocalScaling = compositionLocalOf<Double> { 1.0 }

@Composable
fun App(
    di: DI,
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val settings: DataStore<AppSettings> by di.instance()

    val themeMode by settings.data.map { ThemeMode.ofValue(it.appearance.themeMode) }.collectAsStateSafe {
        ThemeMode.SYSTEM
    }
    val detectedTheme by getSystemDarkMode(systemDarkTheme)
    val useDarkTheme = when (themeMode) {
        is ThemeMode.LIGHT -> false
        is ThemeMode.DARK -> true
        else -> detectedTheme
    }

    val contentColors by settings.data.map { it.appearance.contentColors }.collectAsStateSafe { true }

    CompositionLocalProvider(
        LocalDarkMode provides useDarkTheme,
        LocalContentColors provides contentColors
    ) {
        MaterialTheme(
            colorScheme = if (useDarkTheme) Colors.getDarkScheme() else Colors.getLightScheme(),
            typography = ManropeTypography()
        ) {
            androidx.compose.material.MaterialTheme(
                colors = MaterialTheme.colorScheme.toLegacyColors(useDarkTheme),
                shapes = MaterialTheme.shapes.toLegacyShapes(),
                typography = ManropeTypographyLegacy()
            ) {
                SystemProvider {
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
}

@Composable
expect fun getSystemDarkMode(initValue: Boolean): MutableState<Boolean>

@Composable
expect fun loadImageScheme(key: Any, painter: Painter)

expect val isDesktop: Boolean

@Composable
expect fun SystemProvider(content: @Composable () -> Unit)