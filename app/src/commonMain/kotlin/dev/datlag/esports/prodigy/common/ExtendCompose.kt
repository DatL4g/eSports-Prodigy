package dev.datlag.esports.prodigy.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import dev.datlag.esports.prodigy.model.common.negativeIf
import dev.datlag.esports.prodigy.ui.LocalScaling
import org.kodein.di.DIContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

inline fun Modifier.ifTrue(predicate: Boolean, builder: Modifier.() -> Modifier) = then(if (predicate) builder() else Modifier)
inline fun Modifier.ifFalse(predicate: Boolean, builder: Modifier.() -> Modifier) = then(if (!predicate) builder() else Modifier)

fun LazyGridScope.fullRow(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(span = { GridItemSpan(this.maxLineSpan) }, content = content)
}

fun <T> LazyGridScope.fullRowItems(
    list: List<T>,
    content: @Composable LazyGridItemScope.(T) -> Unit
) {
    items(
        items = list,
        span = {
            GridItemSpan(this.maxLineSpan)
        },
        itemContent = content
    )
}

@Composable
fun Size.toDpSize(minWidth: Float, minHeight: Float): DpSize {
    val sourceWidth = if (this.isUnspecified) {
        0F
    } else {
        this.width
    }
    val sourceHeight = if (this.isUnspecified) {
        0F
    } else {
        this.height
    }

    val targetWidth = max(sourceWidth, minWidth)
    val targetHeight = max(sourceHeight, minHeight)

    return with(LocalDensity.current) {
        DpSize(targetWidth.toDp(), targetHeight.toDp())
    }
}

@Composable
fun Size.toDpSize(minWidth: Dp = 0.dp, minHeight: Dp = 0.dp): DpSize {
    return this.toDpSize(with(LocalDensity.current) {
        minWidth.toPx()
    }, with(LocalDensity.current) {
        minHeight.toPx()
    })
}

@Composable
fun Dp.scaled(min: Dp? = null): Dp {
    return if (this.isUnspecified || this.value <= 0F) {
        this
    } else {
        val newValue = (this.value / LocalScaling.current)
        if (newValue <= (min?.value ?: 0F)) {
            min ?: this
        } else {
            newValue.dp
        }
    }
}

@Composable
fun Number.scaledDp(min: Dp? = null): Dp {
    return this.toDouble().dp.scaled(min)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.tilt(
    maxTilt: Float,
    resetOnPress: Boolean = false,
    onTilt: (x: Float, y: Float) -> Unit = { _, _ -> }
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

fun Size.widthOr(default: Float): Float {
    return if (this.isUnspecified) {
        default
    } else {
        this.width
    }
}

fun Size.heightOr(default: Float): Float {
    return if (this.isUnspecified) {
        default
    } else {
        this.height
    }
}