package dev.datlag.esports.prodigy.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.onClick
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.WindowState
import dev.datlag.esports.prodigy.ui.Orientation
import dev.icerock.moko.resources.FontResource
import dev.icerock.moko.resources.compose.toComposeFont
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun <T> Flow<T>.collectAsStateSafe(initial: () -> T): State<T> = this.collectAsState(initial())

@Composable
actual fun <T> StateFlow<T>.collectAsStateSafe(): State<T> = this.collectAsState()

actual fun FontResource.toComposeFont(
    weight: FontWeight,
    style: FontStyle
): Font = this.toComposeFont(weight, style)

@Composable
fun Orientation.Companion.basedOnSize(windowState: WindowState): Orientation {
    return if (windowState.size.width > windowState.size.height) {
        Orientation.LANDSCAPE
    } else {
        Orientation.PORTRAIT
    }
}

@OptIn(ExperimentalFoundationApi::class)
actual fun Modifier.onClick(
    enabled: Boolean,
    onDoubleClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    onClick: () -> Unit
): Modifier {
    return this.onClick(
        enabled = enabled,
        onDoubleClick = onDoubleClick,
        onLongClick = onLongClick,
        onClick = onClick
    )
}