package dev.datlag.esports.prodigy.color.scheme

import dev.datlag.esports.prodigy.color.hct.Hct
import dev.datlag.esports.prodigy.color.palettes.TonalPalette
import dev.datlag.esports.prodigy.color.utils.MathUtils.sanitizeDegreesDouble


class SchemeTonalSpot(sourceColorHct: Hct, isDark: Boolean, contrastLevel: Double) : DynamicScheme(
    sourceColorHct,
    Variant.TONAL_SPOT,
    isDark,
    contrastLevel,
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 36.0),
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 16.0),
    TonalPalette.fromHueAndChroma(
        sanitizeDegreesDouble(sourceColorHct.hue + 60.0), 24.0
    ),
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 6.0),
    TonalPalette.fromHueAndChroma(sourceColorHct.hue, 8.0)
)