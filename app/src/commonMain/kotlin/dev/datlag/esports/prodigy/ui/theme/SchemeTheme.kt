package dev.datlag.esports.prodigy.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import dev.datlag.esports.prodigy.color.theme.Theme
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.safeEmit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

val LocalDarkMode = compositionLocalOf<Boolean> { error("No dark mode state provided") }

object SchemeTheme {

    val themes: MutableMap<Any, Theme?> = mutableMapOf()

    internal var itemColorScheme: MutableStateFlow<ColorScheme?> = MutableStateFlow(null)

    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = itemColorScheme.value ?: MaterialTheme.colorScheme

    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = colorScheme.toLegacyColors(LocalDarkMode.current)

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val legacyTypography: androidx.compose.material.Typography
        @Composable
        @ReadOnlyComposable
        get() = androidx.compose.material.MaterialTheme.typography

    val shapes: Shapes
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes

    val legacyShapes: androidx.compose.material.Shapes
        @Composable
        @ReadOnlyComposable
        get() = androidx.compose.material.MaterialTheme.shapes

    @Composable
    fun resetColorScheme() {
        resetColorScheme(rememberCoroutineScope())
    }

    fun resetColorScheme(scope: CoroutineScope) {
        itemColorScheme.safeEmit(null, scope)
    }

    @Composable
    fun specificColorScheme(theme: Theme) {
        specificColorScheme(theme, LocalDarkMode.current, rememberCoroutineScope())
    }

    fun specificColorScheme(theme: Theme, darkMode: Boolean, scope: CoroutineScope) {
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

        itemColorScheme.safeEmit(newScheme, scope)
    }
}

@Composable
fun SchemeTheme(content: @Composable () -> Unit) {
    val colorScheme by SchemeTheme.itemColorScheme.collectAsStateSafe()

    MaterialTheme(
        colorScheme = colorScheme ?: SchemeTheme.colorScheme,
        shapes = SchemeTheme.shapes,
        typography = SchemeTheme.typography
    ) {
        androidx.compose.material.MaterialTheme(
            colors = colorScheme?.toLegacyColors(LocalDarkMode.current) ?: SchemeTheme.colors,
            shapes = SchemeTheme.legacyShapes,
            typography = SchemeTheme.legacyTypography
        ) {
            content()
        }
    }
}