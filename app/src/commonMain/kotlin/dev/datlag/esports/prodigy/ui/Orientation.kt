package dev.datlag.esports.prodigy.ui

import androidx.compose.runtime.compositionLocalOf

val LocalOrientation = compositionLocalOf<Orientation> { error("No Orientation state provided") }

sealed interface Orientation {
    object PORTRAIT : Orientation
    object LANDSCAPE : Orientation

    companion object
}