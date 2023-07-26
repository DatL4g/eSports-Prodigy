package dev.datlag.esports.prodigy.model.common

import kotlin.math.abs

fun Float.negativeIf(predicate: Boolean, absoluteSource: Boolean = true): Float {
    val source = if (absoluteSource) {
        abs(this)
    } else {
        this
    }
    return if (predicate) {
        -source
    } else {
        source
    }
}