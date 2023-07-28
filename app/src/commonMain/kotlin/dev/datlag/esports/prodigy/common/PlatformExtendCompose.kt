package dev.datlag.esports.prodigy.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.icerock.moko.resources.FontResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
expect fun <T> Flow<T>.collectAsStateSafe(initial: () -> T): State<T>

@Composable
expect fun <T> StateFlow<T>.collectAsStateSafe(): State<T>

expect fun FontResource.toComposeFont(
    weight: FontWeight = FontWeight.Normal,
    style: FontStyle = FontStyle.Normal
): Font

expect fun Modifier.onClick(
    enabled: Boolean = true,
    onDoubleClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) : Modifier

@Composable
expect fun Modifier.tilt(
    maxTilt: Float,
    resetOnPress: Boolean = false,
    onTilt: (x: Float, y: Float) -> Unit = { _, _ -> }
): Modifier

@Composable
expect fun Tooltip(
    tooltip: @Composable () -> Unit = { },
    content: @Composable () -> Unit
)