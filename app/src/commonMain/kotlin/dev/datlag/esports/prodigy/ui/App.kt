package dev.datlag.esports.prodigy.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import dev.datlag.esports.prodigy.ui.theme.Colors
import dev.datlag.esports.prodigy.ui.theme.*
import org.kodein.di.DI

@Composable
fun App(
    di: DI,
    systemDarkTheme: Boolean = isSystemInDarkTheme() || getSystemDarkMode(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalDarkMode provides systemDarkTheme) {
        MaterialTheme(
            colorScheme = if (systemDarkTheme) Colors.getDarkScheme() else Colors.getLightScheme(),
            typography = ManropeTypography()
        ) {
            androidx.compose.material.MaterialTheme(
                colors = MaterialTheme.colorScheme.toLegacyColors(systemDarkTheme),
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