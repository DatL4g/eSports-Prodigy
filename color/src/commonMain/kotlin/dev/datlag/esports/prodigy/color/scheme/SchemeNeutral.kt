package dev.datlag.esports.prodigy.color.scheme

import dev.datlag.esports.prodigy.color.hct.Hct
import dev.datlag.esports.prodigy.color.palettes.TonalPalette


class SchemeNeutral(sourceColorHct: Hct, isDark: Boolean, contrastLevel: Double) :
    DynamicScheme(
        sourceColorHct,
        Variant.NEUTRAL,
        isDark,
        contrastLevel,
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, 12.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, 8.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, 16.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, 2.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, 2.0)
    )