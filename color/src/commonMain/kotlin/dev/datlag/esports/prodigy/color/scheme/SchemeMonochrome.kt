package dev.datlag.esports.prodigy.color.scheme

import dev.datlag.esports.prodigy.color.hct.Hct
import dev.datlag.esports.prodigy.color.palettes.TonalPalette


class SchemeMonochrome(sourceColorHct: Hct, isDark: Boolean, contrastLevel: Double) :
    DynamicScheme(
        sourceColorHct,
        Variant.MONOCHROME,
        isDark,
        contrastLevel,
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, 0.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, 0.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, 0.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, 0.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, 0.0)
    )