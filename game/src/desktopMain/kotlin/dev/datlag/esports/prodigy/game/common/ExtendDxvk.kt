package dev.datlag.esports.prodigy.game.common

import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache

fun Collection<DxvkStateCache>.containsInvalidEntries(): Boolean {
    return if (this.isEmpty()) {
        false
    } else {
        this.any { it.invalidEntries > 0 }
    }
}