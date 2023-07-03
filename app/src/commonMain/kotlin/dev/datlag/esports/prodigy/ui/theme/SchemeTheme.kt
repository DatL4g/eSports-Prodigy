package dev.datlag.esports.prodigy.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import dev.datlag.esports.prodigy.color.theme.Theme
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.launchIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val LocalDarkMode = compositionLocalOf<Boolean> { error("No dark mode state provided") }

object SchemeTheme {

    internal val itemScheme: MutableStateFlow<Map<Any, ColorScheme?>> = MutableStateFlow(emptyMap())

    fun containsScheme(key: Any): Boolean {
        return itemScheme.value.getOrDefault(key, null) != null
    }

    @Composable
    fun createColorScheme(key: Any, block: suspend CoroutineScope.() -> Theme) {
        val darkMode = LocalDarkMode.current

        rememberCoroutineScope().launchIO {
            createColorScheme(key, block(), darkMode, this)
        }
    }

    @Composable
    fun createColorScheme(key: Any, theme: Theme) {
        createColorScheme(key, theme, LocalDarkMode.current, rememberCoroutineScope())
    }

    fun createColorScheme(key: Any, theme: Theme, darkMode: Boolean, scope: CoroutineScope) {
        val newScheme = if (darkMode) {
            darkColorScheme(
                primary = Color(theme.schemes.dark.primary),
                onPrimary = Color(theme.schemes.dark.onPrimary),
                primaryContainer = Color(theme.schemes.dark.primaryContainer),
                onPrimaryContainer = Color(theme.schemes.dark.onPrimaryContainer),

                secondary = Color(theme.schemes.dark.secondary),
                onSecondary = Color(theme.schemes.dark.onSecondary),
                secondaryContainer = Color(theme.schemes.dark.secondaryContainer),
                onSecondaryContainer = Color(theme.schemes.dark.onSecondaryContainer),

                tertiary = Color(theme.schemes.dark.tertiary),
                onTertiary = Color(theme.schemes.dark.onTertiary),
                tertiaryContainer = Color(theme.schemes.dark.tertiaryContainer),
                onTertiaryContainer = Color(theme.schemes.dark.onTertiaryContainer),

                error = Color(theme.schemes.dark.error),
                onError = Color(theme.schemes.dark.onError),
                errorContainer = Color(theme.schemes.dark.errorContainer),
                onErrorContainer = Color(theme.schemes.dark.onErrorContainer),

                background = Color(theme.schemes.dark.background),
                onBackground = Color(theme.schemes.dark.onBackground),

                surface = Color(theme.schemes.dark.surface),
                onSurface = Color(theme.schemes.dark.onSurface),
                surfaceVariant = Color(theme.schemes.dark.surfaceVariant),
                onSurfaceVariant = Color(theme.schemes.dark.onSurfaceVariant),

                outline = Color(theme.schemes.dark.outline),
                inverseSurface = Color(theme.schemes.dark.inverseSurface),
                inverseOnSurface = Color(theme.schemes.dark.inverseOnSurface),
                inversePrimary = Color(theme.schemes.dark.inversePrimary)
            )
        } else {
            lightColorScheme(
                primary = Color(theme.schemes.light.primary),
                onPrimary = Color(theme.schemes.light.onPrimary),
                primaryContainer = Color(theme.schemes.light.primaryContainer),
                onPrimaryContainer = Color(theme.schemes.light.onPrimaryContainer),

                secondary = Color(theme.schemes.light.secondary),
                onSecondary = Color(theme.schemes.light.onSecondary),
                secondaryContainer = Color(theme.schemes.light.secondaryContainer),
                onSecondaryContainer = Color(theme.schemes.light.onSecondaryContainer),

                tertiary = Color(theme.schemes.light.tertiary),
                onTertiary = Color(theme.schemes.light.onTertiary),
                tertiaryContainer = Color(theme.schemes.light.tertiaryContainer),
                onTertiaryContainer = Color(theme.schemes.light.onTertiaryContainer),

                error = Color(theme.schemes.light.error),
                onError = Color(theme.schemes.light.onError),
                errorContainer = Color(theme.schemes.light.errorContainer),
                onErrorContainer = Color(theme.schemes.light.onErrorContainer),

                background = Color(theme.schemes.light.background),
                onBackground = Color(theme.schemes.light.onBackground),

                surface = Color(theme.schemes.light.surface),
                onSurface = Color(theme.schemes.light.onSurface),
                surfaceVariant = Color(theme.schemes.light.surfaceVariant),
                onSurfaceVariant = Color(theme.schemes.light.onSurfaceVariant),

                outline = Color(theme.schemes.light.outline),
                inverseSurface = Color(theme.schemes.light.inverseSurface),
                inverseOnSurface = Color(theme.schemes.light.inverseOnSurface),
                inversePrimary = Color(theme.schemes.light.inversePrimary),
            )
        }

        scope.launchIO {
            val currentMap = (itemScheme.firstOrNull() ?: itemScheme.value).toMutableMap()
            currentMap[key] = newScheme
            itemScheme.emit(currentMap)
        }
    }
}

@Composable
fun SchemeTheme(key: Any, content: @Composable () -> Unit) {
    val colorScheme by SchemeTheme.itemScheme.map {
        it.firstNotNullOfOrNull { entry ->
            if (entry.key == key) {
                entry.value
            } else {
                null
            }
        }
    }.collectAsStateSafe { null }

    MaterialTheme(
        colorScheme = colorScheme ?: MaterialTheme.colorScheme
    ) {
        androidx.compose.material.MaterialTheme(
            colors = colorScheme?.toLegacyColors(LocalDarkMode.current) ?: androidx.compose.material.MaterialTheme.colors
        ) {
            content()
        }
    }
}