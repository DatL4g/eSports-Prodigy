package dev.datlag.esports.prodigy.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import dev.datlag.esports.prodigy.ui.LocalScaling
import kotlinx.coroutines.launch
import kotlin.math.ln
import kotlin.math.max

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

fun <T> LazyGridScope.fullRowItemsIndexed(
    list: List<T>,
    content: @Composable LazyGridItemScope.(Int, T) -> Unit
) {
    itemsIndexed(
        items = list,
        span = { _, _ ->
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

val DefaultMeasurePolicy: MeasureScope.(measurables: List<Measurable>, constraints: Constraints) -> MeasureResult
    get() = { measurables, constraints ->
        val placeables = measurables.map { measurable -> measurable.measure(constraints) }
        val maxWidth = placeables.maxOf { placeable -> placeable.width }
        val maxHeight = placeables.maxOf { placeable -> placeable.height }

        layout(maxWidth, maxHeight) {
            placeables.forEach { placeable ->
                placeable.place(0, 0)
            }
        }
    }

fun Modifier.shimmer(
    defaultColor: Color = Color.Transparent,
    shimmerColor: Color = Color(0xFF8F8B8B),
    duration: Int = 4000
): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration)
        )
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                defaultColor,
                shimmerColor,
                defaultColor
            ),
            start = Offset(startOffsetX, 0F),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}

fun Modifier.dashedBorder(width: Dp, radius: Dp, color: Color) = this.drawBehind {
    drawIntoCanvas {
        val paint = Paint().apply {
            strokeWidth = width.toPx()
            this.color = color
            style = PaintingStyle.Stroke
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10F, 10F), 0F)
        }
        it.drawRoundRect(
            width.toPx(),
            width.toPx(),
            size.width - width.toPx(),
            size.height - width.toPx(),
            radius.toPx(),
            radius.toPx(),
            paint
        )
    }
}

fun Color.blend(
    blendValue: Float,
    other: Color
): Color {
    if (blendValue <= 0F) return this
    val alpha = ((4.5f * ln(blendValue + 1)) + 2f) / 100f
    return this.copy(alpha = alpha).compositeOver(other)
}

@Composable
fun CornerSize.toDp(shapeSize: Size): Dp = with(LocalDensity.current) {
    this@toDp.toPx(shapeSize, this).toDp()
}

@Composable
fun CornerBasedShape.radiusDp(shapeSize: Size): Dp {
    val topMax = max(this.topStart.toDp(shapeSize), this.topEnd.toDp(shapeSize))
    val bottomMax = max(this.bottomStart.toDp(shapeSize), this.bottomEnd.toDp(shapeSize))

    return max(topMax, bottomMax)
}