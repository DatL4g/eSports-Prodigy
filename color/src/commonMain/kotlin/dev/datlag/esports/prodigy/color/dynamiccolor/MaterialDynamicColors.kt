package dev.datlag.esports.prodigy.color.dynamiccolor

import dev.datlag.esports.prodigy.color.dislike.DislikeAnalyzer.fixIfDisliked
import dev.datlag.esports.prodigy.color.hct.Hct
import dev.datlag.esports.prodigy.color.hct.ViewingConditions
import dev.datlag.esports.prodigy.color.scheme.DynamicScheme
import dev.datlag.esports.prodigy.color.scheme.Variant
import kotlin.math.abs
import kotlin.math.max


object MaterialDynamicColors {

    private const val CONTAINER_ACCENT_TONE_DELTA = 15.0

    fun highestSurface(s: DynamicScheme): DynamicColor {
        return if (s.isDark) surfaceBright() else surfaceDim()
    }

    // Compatibility Keys Colors for Android
    fun primaryPaletteKeyColor(): DynamicColor {
        return DynamicColor.fromPalette({
            it.primaryPalette
        }, {
            it.primaryPalette.keyColor.tone
        })
    }

    fun secondaryPaletteKeyColor(): DynamicColor {
        return DynamicColor.fromPalette({
            it.secondaryPalette
        }, {
            it.secondaryPalette.keyColor.tone
        })
    }

    fun tertiaryPaletteKeyColor(): DynamicColor {
        return DynamicColor.fromPalette({
            it.tertiaryPalette
        }, {
            it.tertiaryPalette.keyColor.tone
        })
    }

    fun neutralPaletteKeyColor(): DynamicColor {
        return DynamicColor.fromPalette({
            it.neutralPalette
        }, {
            it.neutralPalette.keyColor.tone
        })
    }

    fun neutralVariantPaletteKeyColor(): DynamicColor {
        return DynamicColor.fromPalette({
            it.neutralVariantPalette
        }, {
            it.neutralVariantPalette.keyColor.tone
        })
    }

    fun background(): DynamicColor {
        return DynamicColor.fromPalette({
            it.neutralPalette
        }, {
            if (it.isDark) 6.0 else 98.0
        })
    }

    fun onBackground(): DynamicColor {
        return DynamicColor.fromPalette({
            it.neutralPalette
        }, {
           if (it.isDark) 90.0 else 10.0
        }, {
            background()
        })
    }

    fun surface(): DynamicColor {
        return DynamicColor.fromPalette({
            it.neutralPalette
        }, {
            if (it.isDark) 6.0 else 98.0
        })
    }

    fun inverseSurface(): DynamicColor {
        return DynamicColor.fromPalette({
            it.neutralPalette
        }, {
            if (it.isDark) 90.0 else 20.0
        })
    }

    fun surfaceBright(): DynamicColor {
        return DynamicColor.fromPalette({
            it.neutralPalette
        }, {
            if (it.isDark) 24.0 else 98.0
        })
    }

    fun surfaceDim(): DynamicColor {
        return DynamicColor.fromPalette({
            it.neutralPalette
        }, {
            if (it.isDark) 6.0 else 87.0
        })
    }

    fun surfaceContainerLowest(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { s -> if (s.isDark) 4.0 else 100.0 }
    }

    fun surfaceContainerLow(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { s -> if (s.isDark) 10.0 else 96.0 }
    }

    fun surfaceContainer(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { s -> if (s.isDark) 12.0 else 94.0 }
    }

    fun surfaceContainerHigh(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { s -> if (s.isDark) 17.0 else 92.0 }
    }

    fun surfaceContainerHighest(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { s -> if (s.isDark) 22.0 else 90.0 }
    }

    fun onSurface(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.neutralPalette }, { s -> if (s.isDark) 90.0 else 10.0 }, ::highestSurface
        )
    }

    fun inverseOnSurface(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.neutralPalette }, { s -> if (s.isDark) 20.0 else 95.0 }) { inverseSurface() }
    }

    fun surfaceVariant(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralVariantPalette }) { s -> if (s.isDark) 30.0 else 90.0 }
    }

    fun onSurfaceVariant(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.neutralVariantPalette }, { s -> if (s.isDark) 80.0 else 30.0 }) { _ -> surfaceVariant() }
    }

    fun outline(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.neutralVariantPalette }, { s -> if (s.isDark) 60.0 else 50.0 }, ::highestSurface
        )
    }

    fun outlineVariant(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.neutralVariantPalette }, { s -> if (s.isDark) 30.0 else 80.0 }, ::highestSurface
        )
    }

    fun shadow(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { _ -> 0.0 }
    }

    fun scrim(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { _ -> 0.0 }
    }

    fun surfaceTint(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.primaryPalette }) { s -> if (s.isDark) 80.0 else 40.0 }
    }

    fun primaryContainer(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.primaryPalette },
            { s ->
                if (isFidelity(s)) {
                    return@fromPalette performAlbers(s.sourceColorHct, s)
                }
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 85.0 else 25.0
                }
                if (s.isDark) 30.0 else 90.0
            },
            ::highestSurface
        )
    }

    fun onPrimaryContainer(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.primaryPalette },
            { s ->
                if (isFidelity(s)) {
                    return@fromPalette DynamicColor.contrastingTone(primaryContainer().tone.apply(s), 4.5)
                }
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 0.0 else 100.0
                }
                if (s.isDark) 90.0 else 10.0
            },
            { _ -> primaryContainer() },
            null
        )
    }

    fun primary(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.primaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 100.0 else 0.0
                }
                if (s.isDark) 80.0 else 40.0
            },
            ::highestSurface
        ) { s ->
            ToneDeltaConstraint(
                CONTAINER_ACCENT_TONE_DELTA,
                primaryContainer(),
                if (s!!.isDark) TonePolarity.DARKER else TonePolarity.LIGHTER
            )
        }
    }

    fun inversePrimary(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.primaryPalette }, { s -> if (s.isDark) 40.0 else 80.0 }) { _ -> inverseSurface() }
    }

    fun onPrimary(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.primaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 10.0 else 90.0
                }
                if (s.isDark) 20.0 else 100.0
            }
        ) { _ -> primary() }
    }

    fun secondaryContainer(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.secondaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 30.0 else 85.0
                }
                val initialTone = if (s.isDark) 30.0 else 90.0
                if (!isFidelity(s)) {
                    return@fromPalette initialTone
                }
                var answer: Double = findDesiredChromaByTone(
                    s.secondaryPalette.hue,
                    s.secondaryPalette.chroma,
                    initialTone,
                    !s.isDark
                )
                answer = performAlbers(s.secondaryPalette.getHct(answer), s)
                answer
            },
            ::highestSurface
        )
    }

    fun onSecondaryContainer(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.secondaryPalette },
            { s ->
                if (!isFidelity(s)) {
                    return@fromPalette if (s.isDark) 90.0 else 10.0
                }
                DynamicColor.contrastingTone(secondaryContainer().tone.apply(s), 4.5)
            }
        ) { _ -> secondaryContainer() }
    }

    fun secondary(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.secondaryPalette },
            { s -> if (s.isDark) 80.0 else 40.0 },
            ::highestSurface
        ) { s ->
            ToneDeltaConstraint(
                CONTAINER_ACCENT_TONE_DELTA,
                secondaryContainer(),
                if (s!!.isDark) TonePolarity.DARKER else TonePolarity.LIGHTER
            )
        }
    }

    fun onSecondary(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.secondaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 10.0 else 100.0
                }
                if (s.isDark) 20.0 else 100.0
            }
        ) { _ -> secondary() }
    }

    fun tertiaryContainer(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.tertiaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 60.0 else 49.0
                }
                if (!isFidelity(s)) {
                    return@fromPalette if (s.isDark) 30.0 else 90.0
                }
                val albersTone: Double = performAlbers(s.tertiaryPalette.getHct(s.sourceColorHct.tone), s)
                val proposedHct = s.tertiaryPalette.getHct(albersTone)
                fixIfDisliked(proposedHct).tone
            },
            ::highestSurface
        )
    }

    fun onTertiaryContainer(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.tertiaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 0.0 else 100.0
                }
                if (!isFidelity(s)) {
                    return@fromPalette if (s.isDark) 90.0 else 10.0
                }
                DynamicColor.contrastingTone(tertiaryContainer().tone.apply(s), 4.5)
            }
        ) { _ -> tertiaryContainer() }
    }

    fun tertiary(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.tertiaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 90.0 else 25.0
                }
                if (s.isDark) 80.0 else 40.0
            },
            ::highestSurface
        ) { s ->
            ToneDeltaConstraint(
                CONTAINER_ACCENT_TONE_DELTA,
                tertiaryContainer(),
                if (s!!.isDark) TonePolarity.DARKER else TonePolarity.LIGHTER
            )
        }
    }

    fun onTertiary(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.tertiaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 10.0 else 90.0
                }
                if (s.isDark) 20.0 else 100.0
            }
        ) { _ -> tertiary() }
    }

    fun errorContainer(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.errorPalette }, { s -> if (s.isDark) 30.0 else 90.0 }, ::highestSurface
        )
    }

    fun onErrorContainer(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.errorPalette }, { s -> if (s.isDark) 90.0 else 10.0 }) { _ -> errorContainer() }
    }

    fun error(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.errorPalette },
            { s -> if (s.isDark) 80.0 else 40.0 },
            ::highestSurface
        ) { s ->
            ToneDeltaConstraint(
                CONTAINER_ACCENT_TONE_DELTA,
                errorContainer(),
                if (s!!.isDark) TonePolarity.DARKER else TonePolarity.LIGHTER
            )
        }
    }

    fun onError(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.errorPalette }, { s -> if (s.isDark) 20.0 else 100.0 }) { _ -> error() }
    }

    fun primaryFixed(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.primaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 100.0 else 10.0
                }
                90.0
            },
            ::highestSurface
        )
    }

    fun primaryFixedDim(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.primaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 90.0 else 20.0
                }
                80.0
            },
            ::highestSurface
        )
    }

    fun onPrimaryFixed(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.primaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 10.0 else 90.0
                }
                10.0
            }
        ) { _ -> primaryFixedDim() }
    }

    fun onPrimaryFixedVariant(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.primaryPalette },
            { s ->
                if (isMonochrome(s)) {
                    return@fromPalette if (s.isDark) 30.0 else 70.0
                }
                30.0
            }
        ) { _ -> primaryFixedDim() }
    }

    fun secondaryFixed(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.secondaryPalette }, { s -> if (isMonochrome(s)) 80.0 else 90.0 }, ::highestSurface
        )
    }

    fun secondaryFixedDim(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.secondaryPalette }, { s -> if (isMonochrome(s)) 70.0 else 80.0 }, ::highestSurface
        )
    }

    fun onSecondaryFixed(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.secondaryPalette }, { _ -> 10.0 }) { _ -> secondaryFixedDim() }
    }

    fun onSecondaryFixedVariant(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.secondaryPalette },
            { s -> if (isMonochrome(s)) 25.0 else 30.0 }
        ) { _ -> secondaryFixedDim() }
    }

    fun tertiaryFixed(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.tertiaryPalette }, { s -> if (isMonochrome(s)) 40.0 else 90.0 }, ::highestSurface
        )
    }

    fun tertiaryFixedDim(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.tertiaryPalette }, { s -> if (isMonochrome(s)) 30.0 else 80.0 }, ::highestSurface
        )
    }

    fun onTertiaryFixed(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.tertiaryPalette }, { s -> if (isMonochrome(s)) 90.0 else 10.0 }) { _ -> tertiaryFixedDim() }
    }

    fun onTertiaryFixedVariant(): DynamicColor {
        return DynamicColor.fromPalette(
            { s -> s.tertiaryPalette }, { s -> if (isMonochrome(s)) 70.0 else 30.0 }) { _ -> tertiaryFixedDim() }
    }

    /**
     * These colors were present in Android framework before Android U, and used by MDC controls. They
     * should be avoided, if possible. It's unclear if they're used on multiple backgrounds, and if
     * they are, they can't be adjusted for contrast.* For now, they will be set with no background,
     * and those won't adjust for contrast, avoiding issues.
     *
     * <p>* For example, if the same color is on a white background _and_ black background, there's no
     * way to increase contrast with either without losing contrast with the other.
     */
    // colorControlActivated documented as colorAccent in M3 & GM3.
    // colorAccent documented as colorSecondary in M3 and colorPrimary in GM3.
    // Android used Material's Container as Primary/Secondary/Tertiary at launch.
    // Therefore, this is a duplicated version of Primary Container.
    fun controlActivated(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.primaryPalette }, { s -> if (s.isDark) 30.0 else 90.0 }, null)
    }

    // colorControlNormal documented as textColorSecondary in M3 & GM3.
    // In Material, textColorSecondary points to onSurfaceVariant in the non-disabled state,
    // which is Neutral Variant T30/80 in light/dark.
    fun controlNormal(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralVariantPalette }) { s -> if (s.isDark) 80.0 else 30.0 }
    }

    // colorControlHighlight documented, in both M3 & GM3:
    // Light mode: #1f000000 dark mode: #33ffffff.
    // These are black and white with some alpha.
    // 1F hex = 31 decimal; 31 / 255 = 12% alpha.
    // 33 hex = 51 decimal; 51 / 255 = 20% alpha.
    // DynamicColors do not support alpha currently, and _may_ not need it for this use case,
    // depending on how MDC resolved alpha for the other cases.
    // Returning black in dark mode, white in light mode.
    fun controlHighlight(): DynamicColor {
        return DynamicColor(
            { _: DynamicScheme? -> 0.0 },
            { _: DynamicScheme? -> 0.0 },
            { s: DynamicScheme -> if (s.isDark) 100.0 else 0.0 },
            { s: DynamicScheme -> if (s.isDark) 0.20 else 0.12 },
            null,
            { scheme: DynamicScheme? ->
                DynamicColor.toneMinContrastDefault(
                    { s -> if (s.isDark) 100.0 else 0.0 }, null,
                    scheme!!, null
                )
            },
            { scheme: DynamicScheme? ->
                DynamicColor.toneMaxContrastDefault(
                    { s -> if (s.isDark) 100.0 else 0.0 }, null,
                    scheme!!, null
                )
            },
            null
        )
    }

    // textColorPrimaryInverse documented, in both M3 & GM3, documented as N10/N90.
    fun textPrimaryInverse(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { s -> if (s.isDark) 10.0 else 90.0 }
    }

    // textColorSecondaryInverse and textColorTertiaryInverse both documented, in both M3 & GM3, as
    // NV30/NV80
    fun textSecondaryAndTertiaryInverse(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralVariantPalette }) { s -> if (s.isDark) 30.0 else 80.0 }
    }

    // textColorPrimaryInverseDisableOnly documented, in both M3 & GM3, as N10/N90
    fun textPrimaryInverseDisableOnly(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { s -> if (s.isDark) 10.0 else 90.0 }
    }

    // textColorSecondaryInverse and textColorTertiaryInverse in disabled state both documented,
    // in both M3 & GM3, as N10/N90
    fun textSecondaryAndTertiaryInverseDisabled(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { s -> if (s.isDark) 10.0 else 90.0 }
    }

    // textColorHintInverse documented, in both M3 & GM3, as N10/N90
    fun textHintInverse(): DynamicColor {
        return DynamicColor.fromPalette({ s -> s.neutralPalette }) { s -> if (s.isDark) 10.0 else 90.0 }
    }

    private fun viewingConditionsForAlbers(scheme: DynamicScheme): ViewingConditions {
        return ViewingConditions.defaultWithBackgroundLstar(if (scheme.isDark) 30.0 else 80.0)
    }

    private fun isFidelity(scheme: DynamicScheme): Boolean {
        return scheme.variant === Variant.FIDELITY || scheme.variant === Variant.CONTENT
    }

    private fun isMonochrome(scheme: DynamicScheme): Boolean {
        return scheme.variant === Variant.MONOCHROME
    }

    fun findDesiredChromaByTone(
        hue: Double, chroma: Double, tone: Double, byDecreasingTone: Boolean
    ): Double {
        var answer = tone
        var closestToChroma = Hct.from(hue, chroma, tone)
        if (closestToChroma.chroma < chroma) {
            var chromaPeak = closestToChroma.chroma
            while (closestToChroma.chroma < chroma) {
                answer += if (byDecreasingTone) -1.0 else 1.0
                val potentialSolution = Hct.from(hue, chroma, answer)
                if (chromaPeak > potentialSolution.chroma) {
                    break
                }
                if (abs(potentialSolution.chroma - chroma) < 0.4) {
                    break
                }
                val potentialDelta: Double = abs(potentialSolution.chroma - chroma)
                val currentDelta: Double = abs(closestToChroma.chroma - chroma)
                if (potentialDelta < currentDelta) {
                    closestToChroma = potentialSolution
                }
                chromaPeak = max(chromaPeak, potentialSolution.chroma)
            }
        }
        return answer
    }

    fun performAlbers(prealbers: Hct, scheme: DynamicScheme?): Double {
        val albersd = prealbers.inViewingConditions(viewingConditionsForAlbers(scheme!!))
        return if (DynamicColor.tonePrefersLightForeground(prealbers.tone)
            && !DynamicColor.toneAllowsLightForeground(albersd.tone)
        ) {
            DynamicColor.enableLightForeground(prealbers.tone)
        } else {
            DynamicColor.enableLightForeground(albersd.tone)
        }
    }
}