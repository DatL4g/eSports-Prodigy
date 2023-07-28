package dev.datlag.esports.prodigy.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import dev.datlag.esports.prodigy.ui.LocalScaling
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