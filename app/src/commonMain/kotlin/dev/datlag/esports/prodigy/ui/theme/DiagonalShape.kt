package dev.datlag.esports.prodigy.ui.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.tan

data class DiagonalShape(
    val angle: Float = 0F
) : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(Path().apply {
            val angleAbs = abs(angle)

            this.moveTo(angleAbs, 0F)
            this.lineTo(size.width, 0F)
            this.lineTo(size.width - angleAbs, size.height)
            this.lineTo(0F, size.height)
        })
    }
}
