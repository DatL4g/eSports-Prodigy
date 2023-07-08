package dev.datlag.esports.prodigy.ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import dev.datlag.esports.prodigy.color.createTheme
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.other.Constants
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import dev.datlag.esports.prodigy.ui.theme.ThemeDetector
import evalBash
import org.apache.commons.lang3.SystemUtils

@Composable
actual fun getSystemDarkMode(initValue: Boolean): MutableState<Boolean> {
    val detector = remember { ThemeDetector.create() }
    val isDark = remember { mutableStateOf(initValue || detector.isDark) }

    detector.listen { newDark ->
        isDark.value = newDark
    }

    return isDark
}

@Composable
actual fun loadImageScheme(key: Any, painter: Painter) {
    if (!SchemeTheme.containsScheme(key)) {
        val awtImage = painter.toAwtImage(
            LocalDensity.current,
            LayoutDirection.Ltr
        )

        SchemeTheme.createColorScheme(key) {
            awtImage.createTheme()
        }
    }
}

actual val isDesktop: Boolean = true