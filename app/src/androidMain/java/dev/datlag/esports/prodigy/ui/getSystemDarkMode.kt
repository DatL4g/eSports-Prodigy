package dev.datlag.esports.prodigy.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter

@Composable
actual fun getSystemDarkMode(initValue: Boolean): MutableState<Boolean> {
    return remember { mutableStateOf(initValue) }
}

@Composable
actual fun loadImageScheme(key: Any, painter: Painter) {
}

actual val isDesktop: Boolean = false

@Composable
actual fun SystemProvider(content: @Composable () -> Unit) {
    content()
}