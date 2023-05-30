package dev.datlag.esports.prodigy.color.quantize

import dev.datlag.esports.prodigy.color.utils.ColorUtils.argbFromLab
import dev.datlag.esports.prodigy.color.utils.ColorUtils.labFromArgb


class PointProviderLab : PointProvider {
    /**
     * Convert a color represented in ARGB to a 3-element array of L*a*b* coordinates of the color.
     */
    override fun fromInt(argb: Int): DoubleArray {
        val lab = labFromArgb(argb)
        return doubleArrayOf(lab[0], lab[1], lab[2])
    }

    /** Convert a 3-element array to a color represented in ARGB.  */
    override fun toInt(lab: DoubleArray?): Int {
        return argbFromLab(lab!![0], lab[1], lab[2])
    }

    /**
     * Standard CIE 1976 delta E formula also takes the square root, unneeded here. This method is
     * used by quantization algorithms to compare distance, and the relative ordering is the same,
     * with or without a square root.
     *
     *
     * This relatively minor optimization is helpful because this method is called at least once
     * for each pixel in an image.
     */
    override fun distance(one: DoubleArray?, two: DoubleArray?): Double {
        val dL = one!![0] - two!![0]
        val dA = one[1] - two[1]
        val dB = one[2] - two[2]
        return dL * dL + dA * dA + dB * dB
    }
}