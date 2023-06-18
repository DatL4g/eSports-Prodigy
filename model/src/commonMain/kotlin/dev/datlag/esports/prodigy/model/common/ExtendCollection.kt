package dev.datlag.esports.prodigy.model.common

import kotlin.math.max

fun <T> listFrom(vararg list: Collection<T>): List<T> {
    return mutableListOf<T>().apply {
        list.forEach {
            addAll(it)
        }
    }
}

fun <T> T.asList(): List<T> {
    return listOf(this)
}

fun <T> List<T>.safeSubList(from: Int, to: Int): List<T> {
    if (this.isEmpty()) {
        return this
    }

    val safeFrom = if (from < 0) {
        0
    } else if (from > lastIndex) {
        lastIndex
    } else {
        from
    }
    val safeTo = if (to < from) {
        from
    } else if (to > lastIndex) {
        lastIndex
    } else {
        to
    }
    return this.subList(max(safeFrom, 0), max(safeTo, 0))
}