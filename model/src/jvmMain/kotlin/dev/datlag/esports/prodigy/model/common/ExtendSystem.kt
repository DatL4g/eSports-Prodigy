package dev.datlag.esports.prodigy.model.common

import java.io.File

fun systemProperty(key: String): String? = scopeCatching {
    System.getProperty(key).ifEmpty {
        null
    }
}.getOrNull()

fun systemEnv(key: String): String? = scopeCatching {
    System.getenv(key).ifEmpty {
        null
    }
}.getOrNull()

fun homeDirectory(): File? {
    return systemProperty("user.home")?.let {
        File(it)
    } ?: systemEnv("HOME")?.let {
        File(it)
    } ?: systemEnv("\$HOME")?.let {
        File(it)
    }
}