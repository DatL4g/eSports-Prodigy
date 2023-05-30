package dev.datlag.esports.prodigy.color.scheme

import dev.datlag.esports.prodigy.color.dislike.DislikeAnalyzer.fixIfDisliked
import dev.datlag.esports.prodigy.color.hct.Hct
import dev.datlag.esports.prodigy.color.palettes.TonalPalette
import dev.datlag.esports.prodigy.color.temperature.TemperatureCache


class SchemeFidelity(sourceColorHct: Hct, isDark: Boolean, contrastLevel: Double) :
    DynamicScheme(
        sourceColorHct,
        Variant.FIDELITY,
        isDark,
        contrastLevel,
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, sourceColorHct.chroma),
        TonalPalette.fromHueAndChroma(
            sourceColorHct.hue,
            (sourceColorHct.chroma - 32.0).coerceAtLeast(sourceColorHct.chroma * 0.5)
        ),
        TonalPalette.fromHct(
            fixIfDisliked(TemperatureCache(sourceColorHct).complement!!)
        ),
        TonalPalette.fromHueAndChroma(sourceColorHct.hue, sourceColorHct.chroma / 8.0),
        TonalPalette.fromHueAndChroma(
            sourceColorHct.hue, sourceColorHct.chroma / 8.0 + 4.0
        )
    )