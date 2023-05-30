package dev.datlag.esports.prodigy.color.quantize


internal interface Quantizer {
    fun quantize(pixels: IntArray?, maxColors: Int): QuantizerResult
}