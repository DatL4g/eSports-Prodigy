package dev.datlag.esports.prodigy.model.common

fun String.getDigitsOrNull(): String? {
    val replaced = this.replace("\\D+".toRegex(), String())
    return replaced.ifBlank {
        null
    }
}