package dev.datlag.esports.prodigy.color

import android.graphics.Bitmap
import dev.datlag.esports.prodigy.color.quantize.QuantizerCelebi
import dev.datlag.esports.prodigy.color.score.Score
import dev.datlag.esports.prodigy.color.theme.CustomColor
import dev.datlag.esports.prodigy.color.theme.Theme
import dev.datlag.esports.prodigy.color.utils.ThemeUtils

// Slow but copyPixelsToBuffer didn't work as expected
fun ThemeUtils.themeFromImage(image: Bitmap, vararg customColors: CustomColor = emptyArray()): Theme {
    val pixelColors: MutableList<Int> = mutableListOf()

    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val pixel = image.getPixel(x, y)
            var argb = 0
            argb += pixel and 0xff shl 24
            argb += pixel and 0xff
            argb += pixel and 0xff shl 8
            argb += pixel and 0xff shl 16

            pixelColors.add(argb)
        }
    }

    val result = QuantizerCelebi.quantize(pixelColors.toIntArray(), 128)
    val ranked = Score.score(result)
    val top = ranked.firstNotNullOfOrNull {
        if (ignoreColor(it)) {
            null
        } else {
            it
        }
    } ?: ranked[0]

    return themeFromSourceColor(top, *customColors)
}

fun Bitmap.createTheme(vararg customColors: CustomColor = emptyArray()) = ThemeUtils.themeFromImage(this, *customColors)
