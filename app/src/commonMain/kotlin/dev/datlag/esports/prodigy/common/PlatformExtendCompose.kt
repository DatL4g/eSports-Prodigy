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
import java.io.File

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

@Composable
expect fun DragDrop(key: Any, predicate: (File) -> Boolean = { true }, result: (List<File>) -> Unit)