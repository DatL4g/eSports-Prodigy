package dev.datlag.esports.prodigy.game.common

import dev.datlag.esports.prodigy.game.ValveDataFormat
import java.io.File

fun ValveDataFormat.toJsonString(file: File): String {
    return this.toJsonString(file.readText())
}

inline fun <reified T> ValveDataFormat.decodeFromFile(file: File): T {
    return ValveDataFormat.decodeFromString(file.readText())
}