package dev.datlag.esports.prodigy.game.dxvk

sealed class DxvkStateCacheEdition {
    object Standard : DxvkStateCacheEdition()
    object Legacy : DxvkStateCacheEdition()

    fun isLegacy() = when (this) {
        is Standard -> false
        is Legacy -> true
    }
}