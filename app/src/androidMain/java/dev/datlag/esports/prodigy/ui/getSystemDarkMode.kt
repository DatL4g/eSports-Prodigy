package dev.datlag.esports.prodigy.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
actual fun getSystemDarkMode(): Boolean {
    return false
}

@Composable
actual fun loadImageScheme(key: Any, painter: Painter) {
}

actual val isDesktop: Boolean = false