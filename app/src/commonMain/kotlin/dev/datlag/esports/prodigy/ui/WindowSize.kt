package dev.datlag.esports.prodigy.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalWindowSize = compositionLocalOf<WindowSize> { error("No WindowSize provided") }

sealed interface WindowSize {
    object COMPACT : WindowSize
    object MEDIUM : WindowSize
    object EXPANDED : WindowSize

    companion object {
        fun basedOnWidth(windowWidth: Dp): WindowSize {
            return when {
                windowWidth < 600.dp -> COMPACT
                windowWidth < 840.dp -> MEDIUM
                else -> EXPANDED
            }
        }

        fun basedOnHeight(windowHeight: Dp): WindowSize {
            return when {
                windowHeight < 480.dp -> COMPACT
                windowHeight < 900.dp -> MEDIUM
                else -> EXPANDED
            }
        }
    }
}