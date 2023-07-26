package dev.datlag.esports.prodigy.model.common

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.abs

fun <T : Number> T.negativeIf(predicate: Boolean, absoluteSource: Boolean = true): T {
    return if (predicate) {
        negative(absoluteSource)
    } else {
        if (absoluteSource) {
            absolute()
        } else {
            this
        }
    }
}

fun <T : Number> T.absolute(): T {
    return when (this) {
        is Int -> abs(this)
        is Long -> abs(this)
        is Float -> abs(this)
        is Double -> abs(this)
        is BigInteger -> this.abs()
        is BigDecimal -> this.abs()
        else -> this
    } as T
}

fun <T : Number> T.negative(absoluteSource: Boolean = true): T {
    val source = if (absoluteSource) {
        absolute()
    } else {
        this
    }

    return when (source) {
        is Int -> -source
        is Long -> -source
        is Float -> -source
        is Double -> -source
        is BigInteger -> source.negate()
        is BigDecimal -> source.negate()
        else -> source
    } as T
}