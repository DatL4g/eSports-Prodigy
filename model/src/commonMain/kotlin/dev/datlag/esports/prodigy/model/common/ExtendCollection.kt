package dev.datlag.esports.prodigy.model.common

import kotlin.math.max
import kotlin.math.min

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

    val safeFrom = max(min(from, lastIndex), 0)
    return this.subList(
        safeFrom,
        max(safeFrom, min(to, lastIndex))
    )
}