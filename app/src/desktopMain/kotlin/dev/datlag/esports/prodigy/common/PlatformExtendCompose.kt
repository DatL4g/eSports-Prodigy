package dev.datlag.esports.prodigy.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.onClick
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.WindowState
import dev.datlag.esports.prodigy.model.common.negativeIf
import dev.datlag.esports.prodigy.ui.Orientation
import dev.icerock.moko.resources.FontResource
import dev.icerock.moko.resources.compose.toComposeFont
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun Modifier.tilt(
    maxTilt: Float,
    resetOnPress: Boolean,
    onTilt: (x: Float, y: Float) -> Unit
): Modifier {
    var size by remember { mutableStateOf(Size.Unspecified) }
    var posX by remember { mutableStateOf(-1F) }
    var posY by remember { mutableStateOf(-1F) }

    var rotY by remember { mutableStateOf(0F) }
    var rotX by remember { mutableStateOf(0F) }

    var pressed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = size.widthOr(0F), key2 = posX.roundToInt()) {
        val widthMiddle = if (size.isEmpty()) {
            return@LaunchedEffect
        } else {
            size.width / 2
        }

        val absPosX = abs(posX - widthMiddle)
        val percentage = absPosX / widthMiddle
        val tilt = min((percentage * maxTilt), maxTilt)
        rotY = tilt.negativeIf(posX > widthMiddle)
    }

    LaunchedEffect(key1 = size.heightOr(0F), key2 = posY.roundToInt()) {
        val heightMiddle = if (size.isEmpty()) {
            return@LaunchedEffect
        } else {
            size.height / 2
        }

        val absPosY = abs(posY - heightMiddle)
        val percentage = absPosY / heightMiddle
        val tilt = min((percentage * maxTilt), maxTilt)
        rotX = tilt.negativeIf(posY < heightMiddle)
    }

    return this.onSizeChanged {
        size = it.toSize()
    }.onPointerEvent(PointerEventType.Move) { event ->
        event.changes.firstOrNull()?.position?.let {
            posX = it.x
            posY = it.y
        }
    }.onPointerEvent(PointerEventType.Exit) {
        posX = -1F
        posY = -1F
    }.onPointerEvent(PointerEventType.Press) {
        pressed = true
    }.onPointerEvent(PointerEventType.Release) {
        pressed = false
    }.graphicsLayer {
        val tiltY = if (posX < 0F || (resetOnPress && pressed)) {
            0F
        } else {
            rotY
        }

        val tiltX = if (posY < 0F || (resetOnPress && pressed)) {
            0F
        } else {
            rotX
        }

        rotationY = tiltY
        rotationX = tiltX

        onTilt(tiltX, tiltY)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun Tooltip(
    tooltip: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    TooltipArea(
        tooltip = tooltip,
        content = content
    )
}