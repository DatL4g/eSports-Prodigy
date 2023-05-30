package dev.datlag.esports.prodigy.common


inline fun <K, V> Map<K, V>.forEachIndexed(block: (Triple<K, V, Int>) -> Unit) {
    for (i in this.entries.indices) {
        val entry = this.entries.toList()[i]
        block(Triple(entry.key, entry.value, i))
    }
}