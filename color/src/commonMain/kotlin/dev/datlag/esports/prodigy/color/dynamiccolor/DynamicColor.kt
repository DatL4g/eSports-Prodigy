package dev.datlag.esports.prodigy.color.dynamiccolor

import dev.datlag.esports.prodigy.color.common.BiFunction
import dev.datlag.esports.prodigy.color.common.Function
import dev.datlag.esports.prodigy.color.contrast.Contrast
import dev.datlag.esports.prodigy.color.contrast.Contrast.darkerUnsafe
import dev.datlag.esports.prodigy.color.contrast.Contrast.lighterUnsafe
import dev.datlag.esports.prodigy.color.contrast.Contrast.ratioOfTones
import dev.datlag.esports.prodigy.color.hct.Hct
import dev.datlag.esports.prodigy.color.palettes.TonalPalette
import dev.datlag.esports.prodigy.color.scheme.DynamicScheme
import dev.datlag.esports.prodigy.color.utils.MathUtils.clampDouble
import dev.datlag.esports.prodigy.color.utils.MathUtils.clampInt
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


class DynamicColor internal constructor(
    hue: Function<DynamicScheme, Double>,
    chroma: Function<DynamicScheme, Double>,
    tone: Function<DynamicScheme, Double>,
    opacity: Function<DynamicScheme, Double>?,
    background: Function<DynamicScheme, DynamicColor>?,
    toneMinContrast: Function<DynamicScheme, Double>,
    toneMaxContrast: Function<DynamicScheme, Double>,
    toneDeltaConstraint: Function<DynamicScheme?, ToneDeltaConstraint?>?
) {
    val hue: Function<DynamicScheme, Double>
    val chroma: Function<DynamicScheme, Double>
    val tone: Function<DynamicScheme, Double>
    val opacity: Function<DynamicScheme, Double>?
    val background: Function<DynamicScheme, DynamicColor>?
    val toneMinContrast: Function<DynamicScheme, Double>
    val toneMaxContrast: Function<DynamicScheme, Double>
    val toneDeltaConstraint: Function<DynamicScheme?, ToneDeltaConstraint?>?
    private val hctCache = HashMap<DynamicScheme, Hct>()

    /**
     * The base constructor for DynamicColor.
     *
     *
     * Functional arguments allow overriding without risks that come with subclasses. _Strongly_
     * prefer using one of the static convenience constructors. This class is arguably too flexible to
     * ensure it can support any scenario.
     *
     *
     * For example, the default behavior of adjust tone at max contrast to be at a 7.0 ratio with
     * its background is principled and matches a11y guidance. That does not mean it's the desired
     * approach for _every_ design system, and every color pairing, always, in every case.
     *
     * @param hue given DynamicScheme, return the hue in HCT of the output color.
     * @param chroma given DynamicScheme, return chroma in HCT of the output color.
     * @param tone given DynamicScheme, return tone in HCT of the output color.
     * @param background given DynamicScheme, return the DynamicColor that is the background of this
     * DynamicColor. When this is provided, automated adjustments to lower and raise contrast are
     * made.
     * @param toneMinContrast given DynamicScheme, return tone in HCT/L* in L*a*b* this color should
     * be at minimum contrast. See toneMinContrastDefault for the default behavior, and strongly
     * consider using it unless you have strong opinions on a11y. The static constructors use it.
     * @param toneMaxContrast given DynamicScheme, return tone in HCT/L* in L*a*b* this color should
     * be at maximum contrast. See toneMaxContrastDefault for the default behavior, and strongly
     * consider using it unless you have strong opinions on a11y. The static constructors use it.
     * @param toneDeltaConstraint given DynamicScheme, return a ToneDeltaConstraint instance that
     * describes a requirement that this DynamicColor must always have some difference in tone/L*
     * from another DynamicColor.<br></br>
     * Unlikely to be useful unless a design system has some distortions where colors that don't
     * have a background/foreground relationship must have _some_ difference in tone, yet, not
     * enough difference to create meaningful contrast.
     */
    init {
        this.hue = hue
        this.chroma = chroma
        this.tone = tone
        this.opacity = opacity
        this.background = background
        this.toneMinContrast = toneMinContrast
        this.toneMaxContrast = toneMaxContrast
        this.toneDeltaConstraint = toneDeltaConstraint
    }

    fun getArgb(scheme: DynamicScheme): Int {
        val argb = getHct(scheme).toInt()
        if (opacity == null) {
            return argb
        }
        val percentage: Double = opacity.apply(scheme)
        val alpha = clampInt(0, 255, (percentage * 255).roundToInt())
        return argb and 0x00ffffff or (alpha shl 24)
    }

    fun getHct(scheme: DynamicScheme): Hct {
        val cachedAnswer = hctCache[scheme]
        if (cachedAnswer != null) {
            return cachedAnswer
        }
        // This is crucial for aesthetics: we aren't simply the taking the standard color
        // and changing its tone for contrast. Rather, we find the tone for contrast, then
        // use the specified chroma from the palette to construct a new color.
        //
        // For example, this enables colors with standard tone of T90, which has limited chroma, to
        // "recover" intended chroma as contrast increases.
        val answer = Hct.from(hue.apply(scheme), chroma.apply(scheme), getTone(scheme))
        // NOMUTANTS--trivial test with onerous dependency injection requirement.
        if (hctCache.size > 4) {
            hctCache.clear()
        }
        // NOMUTANTS--trivial test with onerous dependency injection requirement.
        hctCache[scheme] = answer
        return answer
    }

    /** Returns the tone in HCT, ranging from 0 to 100, of the resolved color given scheme.  */
    fun getTone(scheme: DynamicScheme): Double {
        var answer: Double = tone.apply(scheme)
        val decreasingContrast = scheme.contrastLevel < 0.0
        if (scheme.contrastLevel != 0.0) {
            val startTone: Double = tone.apply(scheme)
            val endTone: Double =
                if (decreasingContrast) toneMinContrast.apply(scheme) else toneMaxContrast.apply(scheme)
            val delta = (endTone - startTone) * abs(scheme.contrastLevel)
            answer = delta + startTone
        }
        val bgDynamicColor: DynamicColor? = background?.apply(scheme)
        var minRatio = Contrast.RATIO_MIN
        var maxRatio = Contrast.RATIO_MAX
        if (bgDynamicColor != null) {
            val bgHasBg = bgDynamicColor.background != null && bgDynamicColor.background.apply(scheme) != null
            val standardRatio = ratioOfTones(tone.apply(scheme), bgDynamicColor.tone.apply(scheme))
            if (decreasingContrast) {
                val minContrastRatio = ratioOfTones(
                    toneMinContrast.apply(scheme), bgDynamicColor.toneMinContrast.apply(scheme)
                )
                minRatio = if (bgHasBg) minContrastRatio else 1.0
                maxRatio = standardRatio
            } else {
                val maxContrastRatio = ratioOfTones(
                    toneMaxContrast.apply(scheme), bgDynamicColor.toneMaxContrast.apply(scheme)
                )
                minRatio = if (bgHasBg) min(maxContrastRatio, standardRatio) else 1.0
                maxRatio = if (bgHasBg) max(maxContrastRatio, standardRatio) else 21.0
            }
        }
        val finalMinRatio = minRatio
        val finalMaxRatio = maxRatio
        val finalAnswer = answer
        answer = calculateDynamicTone(
            scheme,
            tone,
            { dynamicColor -> dynamicColor.getTone(scheme) },
            { _, _ -> finalAnswer },
            if (bgDynamicColor != null) { _ -> bgDynamicColor } else null,
            toneDeltaConstraint,
            { finalMinRatio },
            { finalMaxRatio })
        return answer
    }

    companion object {
        /**
         * Create a DynamicColor from a hex code.
         *
         *
         * Result has no background; thus no support for increasing/decreasing contrast for a11y.
         */
        fun fromArgb(argb: Int): DynamicColor {
            val hct = Hct.fromInt(argb)
            val palette = TonalPalette.fromInt(argb)
            return fromPalette({ palette }) { hct.tone }
        }

        /**
         * Create a DynamicColor from just a hex code.
         *
         *
         * Result has no background; thus cannot support increasing/decreasing contrast for a11y.
         *
         * @param argb A hex code.
         * @param tone Function that provides a tone given DynamicScheme. Useful for adjusting for dark
         * vs. light mode.
         */
        fun fromArgb(argb: Int, tone: Function<DynamicScheme, Double>): DynamicColor {
            return fromPalette({
                TonalPalette.fromInt(
                    argb
                )
            }, tone)
        }

        /**
         * Create a DynamicColor.
         *
         *
         * If you don't understand HCT fully, or your design team doesn't, but wants support for
         * automated contrast adjustment, this method is _extremely_ useful: you can take a standard
         * design system expressed as hex codes, create DynamicColors corresponding to each color, and
         * then wire up backgrounds.
         *
         *
         * If the design system uses the same hex code on multiple backgrounds, define that in multiple
         * DynamicColors so that the background is accurate for each one. If you define a DynamicColor
         * with one background, and actually use it on another, DynamicColor can't guarantee contrast. For
         * example, if you use a color on both black and white, increasing the contrast on one necessarily
         * decreases contrast of the other.
         *
         * @param argb A hex code.
         * @param tone Function that provides a tone given DynamicScheme. (useful for dark vs. light mode)
         * @param background Function that provides background DynamicColor given DynamicScheme. Useful
         * for contrast, given a background, colors can adjust to increase/decrease contrast.
         */
        fun fromArgb(
            argb: Int,
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor>?
        ): DynamicColor {
            return fromPalette({
                TonalPalette.fromInt(
                    argb
                )
            }, tone, background)
        }

        /**
         * Create a DynamicColor from:
         *
         * @param argb A hex code.
         * @param tone Function that provides a tone given DynamicScheme. (useful for dark vs. light mode)
         * @param background Function that provides background DynamicColor given DynamicScheme. Useful
         * for contrast, given a background, colors can adjust to increase/decrease contrast.
         * @param toneDeltaConstraint Function that provides a ToneDeltaConstraint given DynamicScheme.
         * Useful for ensuring lightness difference between colors that don't _require_ contrast or
         * have a formal background/foreground relationship.
         */
        fun fromArgb(
            argb: Int,
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor>?,
            toneDeltaConstraint: Function<DynamicScheme?, ToneDeltaConstraint?>?
        ): DynamicColor {
            return fromPalette(
                {
                    TonalPalette.fromInt(
                        argb
                    )
                }, tone, background, toneDeltaConstraint
            )
        }

        /**
         * Create a DynamicColor.
         *
         * @param palette Function that provides a TonalPalette given DynamicScheme. A TonalPalette is
         * defined by a hue and chroma, so this replaces the need to specify hue/chroma. By providing
         * a tonal palette, when contrast adjustments are made, intended chroma can be preserved. For
         * example, at T/L* 90, there is a significant limit to the amount of chroma. There is no
         * colorful red, a red that light is pink. By preserving the _intended_ chroma if lightness
         * lowers for contrast adjustments, the intended chroma is restored.
         * @param tone Function that provides a tone given DynamicScheme. (useful for dark vs. light mode)
         */
        fun fromPalette(
            palette: Function<DynamicScheme, TonalPalette>, tone: Function<DynamicScheme, Double>
        ): DynamicColor {
            return fromPalette(palette, tone, null, null)
        }

        /**
         * Create a DynamicColor.
         *
         * @param palette Function that provides a TonalPalette given DynamicScheme. A TonalPalette is
         * defined by a hue and chroma, so this replaces the need to specify hue/chroma. By providing
         * a tonal palette, when contrast adjustments are made, intended chroma can be preserved. For
         * example, at T/L* 90, there is a significant limit to the amount of chroma. There is no
         * colorful red, a red that light is pink. By preserving the _intended_ chroma if lightness
         * lowers for contrast adjustments, the intended chroma is restored.
         * @param tone Function that provides a tone given DynamicScheme. (useful for dark vs. light mode)
         * @param background Function that provides background DynamicColor given DynamicScheme. Useful
         * for contrast, given a background, colors can adjust to increase/decrease contrast.
         */
        fun fromPalette(
            palette: Function<DynamicScheme, TonalPalette>,
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor>?
        ): DynamicColor {
            return fromPalette(palette, tone, background, null)
        }

        /**
         * Create a DynamicColor.
         *
         * @param palette Function that provides a TonalPalette given DynamicScheme. A TonalPalette is
         * defined by a hue and chroma, so this replaces the need to specify hue/chroma. By providing
         * a tonal palette, when contrast adjustments are made, intended chroma can be preserved. For
         * example, at T/L* 90, there is a significant limit to the amount of chroma. There is no
         * colorful red, a red that light is pink. By preserving the _intended_ chroma if lightness
         * lowers for contrast adjustments, the intended chroma is restored.
         * @param tone Function that provides a tone given DynamicScheme. (useful for dark vs. light mode)
         * @param background Function that provides background DynamicColor given DynamicScheme. Useful
         * for contrast, given a background, colors can adjust to increase/decrease contrast.
         * @param toneDeltaConstraint Function that provides a ToneDeltaConstraint given DynamicScheme.
         * Useful for ensuring lightness difference between colors that don't _require_ contrast or
         * have a formal background/foreground relationship.
         */
        fun fromPalette(
            palette: Function<DynamicScheme, TonalPalette>,
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor>?,
            toneDeltaConstraint: Function<DynamicScheme?, ToneDeltaConstraint?>?
        ): DynamicColor {
            return DynamicColor(
                { scheme -> palette.apply(scheme).hue },
                { scheme -> palette.apply(scheme).chroma },
                tone,
                null,
                background,
                { scheme ->
                    toneMinContrastDefault(
                        tone,
                        background,
                        scheme,
                        toneDeltaConstraint
                    )
                },
                { scheme ->
                    toneMaxContrastDefault(
                        tone,
                        background,
                        scheme,
                        toneDeltaConstraint
                    )
                },
                toneDeltaConstraint
            )
        }

        /**
         * The default algorithm for calculating the tone of a color at minimum contrast.<br></br>
         * If the original contrast ratio was >= 7.0, reach contrast 4.5.<br></br>
         * If the original contrast ratio was >= 3.0, reach contrast 3.0.<br></br>
         * If the original contrast ratio was < 3.0, reach that ratio.
         */
        fun toneMinContrastDefault(
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor>?,
            scheme: DynamicScheme,
            toneDeltaConstraint: Function<DynamicScheme?, ToneDeltaConstraint?>?
        ): Double {
            return calculateDynamicTone(
                scheme,
                tone,
                { c -> c.toneMinContrast.apply(scheme) },
                { stdRatio: Double, bgTone: Double ->
                    var answer: Double = tone.apply(scheme)
                    if (stdRatio >= Contrast.RATIO_70) {
                        answer = contrastingTone(
                            bgTone,
                            Contrast.RATIO_45
                        )
                    } else if (stdRatio >= Contrast.RATIO_30) {
                        answer = contrastingTone(
                            bgTone,
                            Contrast.RATIO_30
                        )
                    } else {
                        val backgroundHasBackground =
                            background != null && background.apply(scheme).background != null
                                    && background.apply(scheme).background?.apply(scheme) != null
                        if (backgroundHasBackground) {
                            answer = contrastingTone(bgTone, stdRatio)
                        }
                    }
                    answer
                },
                background,
                toneDeltaConstraint,
                null,
                { standardRatio -> standardRatio })
        }

        /**
         * The default algorithm for calculating the tone of a color at maximum contrast.<br></br>
         * If the color's background has a background, reach contrast 7.0.<br></br>
         * If it doesn't, maintain the original contrast ratio.<br></br>
         *
         *
         * This ensures text on surfaces maintains its original, often detrimentally excessive,
         * contrast ratio. But, text on buttons can soften to not have excessive contrast.
         *
         *
         * Historically, digital design uses pure whites and black for text and surfaces. It's too much
         * of a jump at this point in history to introduce a dynamic contrast system _and_ insist that
         * text always had excessive contrast and should reach 7.0, it would deterimentally affect desire
         * to understand and use dynamic contrast.
         */
        fun toneMaxContrastDefault(
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor>?,
            scheme: DynamicScheme,
            toneDeltaConstraint: Function<DynamicScheme?, ToneDeltaConstraint?>?
        ): Double {
            return calculateDynamicTone(
                scheme,
                tone,
                Function<DynamicColor, Double> { c -> c.toneMaxContrast.apply(scheme) },
                { stdRatio: Double, bgTone: Double ->
                    val backgroundHasBackground =
                        background != null && background.apply(scheme).background != null
                                && background.apply(scheme).background?.apply(scheme) != null
                    if (backgroundHasBackground) {
                        return@calculateDynamicTone contrastingTone(
                            bgTone,
                            Contrast.RATIO_70
                        )
                    } else {
                        return@calculateDynamicTone contrastingTone(
                            bgTone,
                            max(Contrast.RATIO_70, stdRatio)
                        )
                    }
                },
                background,
                toneDeltaConstraint,
                null,
                null
            )
        }

        /**
         * Core method for calculating a tone for under dynamic contrast.
         *
         *
         * It enforces important properties:<br></br>
         * #1. Desired contrast ratio is reached.<br></br>
         * As contrast increases from standard to max, the tones involved should always be at least the
         * standard ratio. For example, if a button is T90, and button text is T0, and the button is T0 at
         * max contrast, the button text cannot simply linearly interpolate from T0 to T100, or at some
         * point they'll both be at the same tone.
         *
         *
         * #2. Enable light foregrounds on midtones.<br></br>
         * The eye prefers light foregrounds on T50 to T60, possibly up to T70, but, contrast ratio 4.5
         * can't be reached with T100 unless the foreground is T50. Contrast ratio 4.5 is crucial, it
         * represents 'readable text', i.e. text smaller than ~40 dp / 1/4". So, if a tone is between T50
         * and T60, it is proactively changed to T49 to enable light foregrounds.
         *
         *
         * #3. Ensure tone delta with another color.<br></br>
         * In design systems, there may be colors that don't have a pure background/foreground
         * relationship, but, do require different tones for visual differentiation. ToneDeltaConstraint
         * models this requirement, and DynamicColor enforces it.
         */
        fun calculateDynamicTone(
            scheme: DynamicScheme,
            toneStandard: Function<DynamicScheme, Double>,
            toneToJudge: Function<DynamicColor, Double>,
            desiredTone: BiFunction<Double, Double, Double>,
            background: Function<DynamicScheme, DynamicColor>?,
            toneDeltaConstraint: Function<DynamicScheme?, ToneDeltaConstraint?>?,
            minRatio: Function<Double, Double>?,
            maxRatio: Function<Double, Double>?
        ): Double {
            // Start with the tone with no adjustment for contrast.
            // If there is no background, don't perform any adjustment, return immediately.
            val toneStd: Double = toneStandard.apply(scheme)
            var answer = toneStd
            val bgDynamic: DynamicColor = (if (background == null) null else background.apply(scheme)) ?: return answer
            val bgToneStd: Double = bgDynamic.tone.apply(scheme)
            val stdRatio = ratioOfTones(toneStd, bgToneStd)

            // If there is a background, determine its tone after contrast adjustment.
            // Then, calculate the foreground tone that ensures the caller's desired contrast ratio is met.
            val bgTone: Double = toneToJudge.apply(bgDynamic)
            val myDesiredTone = desiredTone.apply(stdRatio, bgTone)
            val currentRatio = ratioOfTones(bgTone, myDesiredTone)
            val minRatioRealized =
                if (minRatio == null) Contrast.RATIO_MIN else if (minRatio.apply(stdRatio) == null) Contrast.RATIO_MIN else minRatio.apply(
                    stdRatio
                )
            val maxRatioRealized =
                if (maxRatio == null) Contrast.RATIO_MAX else if (maxRatio.apply(stdRatio) == null) Contrast.RATIO_MAX else maxRatio.apply(
                    stdRatio
                )
            val desiredRatio = clampDouble(minRatioRealized, maxRatioRealized, currentRatio)
            answer = if (desiredRatio == currentRatio) {
                myDesiredTone
            } else {
                contrastingTone(bgTone, desiredRatio)
            }

            // If the background has no background,  adjust the foreground tone to ensure that
            // it is dark enough to have a light foreground.
            if (bgDynamic.background == null) {
                answer = enableLightForeground(answer)
            }

            // If the caller has specified a constraint where it must have a certain  tone distance from
            // another color, enforce that constraint.
            answer = ensureToneDelta(answer, toneStd, scheme, toneDeltaConstraint, toneToJudge)
            return answer
        }

        fun ensureToneDelta(
            tone: Double,
            toneStandard: Double,
            scheme: DynamicScheme,
            toneDeltaConstraint: Function<DynamicScheme?, ToneDeltaConstraint?>?,
            toneToDistanceFrom: Function<DynamicColor, Double>
        ): Double {
            val constraint: ToneDeltaConstraint =
                (if (toneDeltaConstraint == null) null else toneDeltaConstraint.apply(scheme))
                    ?: return tone
            val requiredDelta = constraint.delta
            val keepAwayTone: Double = toneToDistanceFrom.apply(constraint.keepAway)
            val delta = abs(tone - keepAwayTone)
            return if (delta >= requiredDelta) {
                tone
            } else when (constraint.keepAwayPolarity) {
                TonePolarity.DARKER -> clampDouble(0.0, 100.0, keepAwayTone + requiredDelta)
                TonePolarity.LIGHTER -> clampDouble(0.0, 100.0, keepAwayTone - requiredDelta)
                TonePolarity.NO_PREFERENCE -> {
                    val keepAwayToneStandard: Double = constraint.keepAway.tone.apply(scheme)
                    val preferLighten = toneStandard > keepAwayToneStandard
                    val alterAmount = abs(delta - requiredDelta)
                    val lighten = if (preferLighten) tone + alterAmount <= 100.0 else tone < alterAmount
                    if (lighten) tone + alterAmount else tone - alterAmount
                }
            }
            return tone
        }

        /**
         * Given a background tone, find a foreground tone, while ensuring they reach a contrast ratio
         * that is as close to ratio as possible.
         */
        fun contrastingTone(bgTone: Double, ratio: Double): Double {
            val lighterTone = lighterUnsafe(bgTone, ratio)
            val darkerTone = darkerUnsafe(bgTone, ratio)
            val lighterRatio = ratioOfTones(lighterTone, bgTone)
            val darkerRatio = ratioOfTones(darkerTone, bgTone)
            val preferLighter = tonePrefersLightForeground(bgTone)
            return if (preferLighter) {
                // "Neglible difference" handles an edge case where the initial contrast ratio is high
                // (ex. 13.0), and the ratio passed to the function is that high ratio, and both the lighter
                // and darker ratio fails to pass that ratio.
                //
                // This was observed with Tonal Spot's On Primary Container turning black momentarily between
                // high and max contrast in light mode. PC's standard tone was T90, OPC's was T10, it was
                // light mode, and the contrast level was 0.6568521221032331.
                val negligibleDifference =
                    abs(lighterRatio - darkerRatio) < 0.1 && lighterRatio < ratio && darkerRatio < ratio
                if (lighterRatio >= ratio || lighterRatio >= darkerRatio || negligibleDifference) {
                    lighterTone
                } else {
                    darkerTone
                }
            } else {
                if (darkerRatio >= ratio || darkerRatio >= lighterRatio) darkerTone else lighterTone
            }
        }

        /**
         * Adjust a tone down such that white has 4.5 contrast, if the tone is reasonably close to
         * supporting it.
         */
        fun enableLightForeground(tone: Double): Double {
            return if (tonePrefersLightForeground(tone) && !toneAllowsLightForeground(tone)) {
                49.0
            } else tone
        }

        /**
         * People prefer white foregrounds on ~T60-70. Observed over time, and also by Andrew Somers
         * during research for APCA.
         *
         *
         * T60 used as to create the smallest discontinuity possible when skipping down to T49 in order
         * to ensure light foregrounds.
         */
        fun tonePrefersLightForeground(tone: Double): Boolean {
            return tone.roundToInt() < 60
        }

        /** Tones less than ~T50 always permit white at 4.5 contrast.  */
        fun toneAllowsLightForeground(tone: Double): Boolean {
            return tone.roundToInt() <= 49
        }
    }
}