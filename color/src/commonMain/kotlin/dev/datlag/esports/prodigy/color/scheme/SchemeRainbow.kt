package dev.datlag.esports.prodigy.color.scheme

import dev.datlag.esports.prodigy.color.hct.Hct
import dev.datlag.esports.prodigy.color.palettes.TonalPalette
import dev.datlag.esports.prodigy.color.utils.MathUtils

class SchemeRainbow(
    sourceColorHct: Hct,
    isDark: Boolean,
    contrastLevel: Double
) : DynamicScheme(
    sourceColorHct,
    Variant.RAINBOW,
    isDark,
    contrastLevel,
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 48.0),
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 16.0),
    TonalPalette.fromHueAndChroma(
        MathUtils.sanitizeDegreesDouble(sourceColorHct.hue + 60), 24.0
    ),
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 0.0),
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 0.0)
)