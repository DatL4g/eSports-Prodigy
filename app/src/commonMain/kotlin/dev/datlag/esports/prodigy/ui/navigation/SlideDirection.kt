package dev.datlag.esports.prodigy.ui.navigation

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import dev.datlag.esports.prodigy.model.common.negative

sealed interface SlideDirection {

    fun offset(factor: Float, layoutDirection: LayoutDirection): Modifier

    object FromTopToBottom : SlideDirection {
        override fun offset(factor: Float, layoutDirection: LayoutDirection) = Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)

            layout(placeable.width, placeable.height) {
                placeable.placeRelative(y = (placeable.height.toFloat() * factor).toInt().negative(), x = 0)
            }
        }
    }

    object FromBottomToTop : SlideDirection {
        override fun offset(factor: Float, layoutDirection: LayoutDirection) = Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)

            layout(placeable.width, placeable.height) {
                placeable.placeRelative(y = (placeable.height.toFloat() * factor).toInt(), x = 0)
            }
        }
    }

    data class StartToEnd(
        private val directionAware: Boolean
    ) : SlideDirection {
        override fun offset(
            factor: Float,
            layoutDirection: LayoutDirection
        ): Modifier = Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            val positionX = (placeable.width.toFloat() * factor).toInt()
            val directionAwarePositionX = when {
                directionAware && layoutDirection == LayoutDirection.Rtl -> positionX
                else -> positionX.unaryMinus()
            }

            layout(placeable.width, placeable.height) {
                placeable.placeRelative(x = directionAwarePositionX, y = 0)
            }
        }
    }

    data class EndToStart(
        private val directionAware: Boolean
    ) : SlideDirection {
        override fun offset(
            factor: Float,
            layoutDirection: LayoutDirection
        ): Modifier = Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            val positionX = (placeable.width.toFloat() * factor).toInt()
            val directionAwarePositionX = when {
                directionAware && layoutDirection == LayoutDirection.Rtl -> positionX.unaryMinus()
                else -> positionX
            }

            layout(placeable.width, placeable.height) {
                placeable.placeRelative(x = directionAwarePositionX, y = 0)
            }
        }
    }

    companion object {
        val EndToStart = EndToStart(directionAware = true)
        val StartToEnd = StartToEnd(directionAware = true)
    }
}