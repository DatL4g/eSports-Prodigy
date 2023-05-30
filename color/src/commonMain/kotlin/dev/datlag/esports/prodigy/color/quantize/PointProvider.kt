package dev.datlag.esports.prodigy.color.quantize


interface PointProvider {
    fun fromInt(argb: Int): DoubleArray?
    fun toInt(point: DoubleArray?): Int
    fun distance(a: DoubleArray?, b: DoubleArray?): Double
}