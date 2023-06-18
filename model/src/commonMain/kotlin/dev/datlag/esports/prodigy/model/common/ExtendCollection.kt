package dev.datlag.esports.prodigy.model.common

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