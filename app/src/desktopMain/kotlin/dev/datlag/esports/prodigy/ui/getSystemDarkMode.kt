package dev.datlag.esports.prodigy.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import dev.datlag.esports.prodigy.color.createTheme
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.other.Constants
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import evalBash
import org.apache.commons.lang3.SystemUtils

@Composable
actual fun getSystemDarkMode(): Boolean {
    return if (SystemUtils.IS_OS_LINUX) {
        (Constants.LINUX_DARK_MODE_CMD.evalBash(env = null).getOrDefault(String())).ifEmpty {
            Constants.LINUX_DARK_MODE_LEGACY_CMD.evalBash(env = null).getOrDefault(String())
        }.contains("dark", true)
    } else {
        false
    }
}

@Composable
actual fun loadImageScheme(key: Any, painter: Painter) {
    if (SchemeTheme.themes.contains(key)) {
        val awtImage = painter.toAwtImage(
            LocalDensity.current,
            LayoutDirection.Ltr
        )

        rememberCoroutineScope().launchIO {
            SchemeTheme.themes[key] = awtImage.createTheme()
        }
    }
}

actual val isDesktop: Boolean = true