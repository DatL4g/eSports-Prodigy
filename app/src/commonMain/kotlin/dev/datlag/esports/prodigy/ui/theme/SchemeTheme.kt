package dev.datlag.esports.prodigy.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import dev.datlag.esports.prodigy.color.theme.Theme
import dev.datlag.esports.prodigy.common.getValueBlocking
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

val LocalDarkMode = compositionLocalOf<Boolean> { error("No dark mode state provided") }
val LocalContentColors = compositionLocalOf<Boolean> { error("No content color state provided") }

object SchemeTheme {

    internal val itemScheme: MutableStateFlow<Map<Any, ThemeHolder?>> = MutableStateFlow(emptyMap())

    fun containsScheme(key: Any): Boolean {
        return itemScheme.value.getOrDefault(key, null) != null
    }

    @Composable
    fun createColorScheme(key: Any, block: suspend CoroutineScope.() -> Theme) {
        LaunchedEffect(key) {
            createColorScheme(key, block(), this)
        }
    }

    @Composable
    fun createColorScheme(key: Any, theme: Theme) {
        createColorScheme(key, theme, rememberCoroutineScope())
    }

    fun createColorScheme(key: Any, theme: Theme, scope: CoroutineScope) {
        val newTheme = ThemeHolder(
            dark = darkColorScheme(
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
            ),
            light = lightColorScheme(
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
        )

        scope.launchIO {
            val currentMap = (itemScheme.firstOrNull() ?: itemScheme.value).toMutableMap()
            currentMap[key] = newTheme
            itemScheme.emit(currentMap)
        }
    }
}

@Composable
fun SchemeTheme(key: Any?, content: @Composable () -> Unit) {
    if (LocalContentColors.current) {
        val themeHolder by SchemeTheme.itemScheme.map {
            it.firstNotNullOfOrNull { entry ->
                if (entry.key == key) {
                    entry.value
                } else {
                    null
                }
            }
        }.collectAsStateWithLifecycle(initialValue = null)

        val scheme = (if (LocalDarkMode.current) themeHolder?.dark else themeHolder?.light) ?: MaterialTheme.colorScheme

        MaterialTheme(
            colorScheme = scheme
        ) {
            androidx.compose.material.MaterialTheme(
                colors = scheme.toLegacyColors(LocalDarkMode.current)
            ) {
                SchemeThemeSystemProvider(scheme) {
                    content()
                }
            }
        }
    } else {
        content()
    }
}

data class ThemeHolder(
    val dark: ColorScheme,
    val light: ColorScheme
)

@Composable
expect fun SchemeThemeSystemProvider(scheme: ColorScheme, content: @Composable () -> Unit)