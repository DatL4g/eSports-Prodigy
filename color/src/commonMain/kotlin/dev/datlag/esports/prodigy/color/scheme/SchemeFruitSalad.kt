package dev.datlag.esports.prodigy.color.scheme

import dev.datlag.esports.prodigy.color.hct.Hct
import dev.datlag.esports.prodigy.color.palettes.TonalPalette
import dev.datlag.esports.prodigy.color.utils.MathUtils

class SchemeFruitSalad(
    sourceColorHct: Hct,
    isDark: Boolean,
    contrastLevel: Double
) : DynamicScheme(
    sourceColorHct,
    Variant.FRUIT_SALAD,
    isDark,
    contrastLevel,
    TonalPalette.fromHueAndChroma(
        MathUtils.sanitizeDegreesDouble(sourceColorHct.hue - 50.0), 48.0
    ),
    TonalPalette.fromHueAndChroma(
        MathUtils.sanitizeDegreesDouble(sourceColorHct.hue - 50.0), 36.0
    ),
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 36.0),
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 10.0),
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 16.0)
)