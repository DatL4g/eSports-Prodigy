package dev.datlag.esports.prodigy.ui.screen.home

sealed interface RevealKeys {
    data object Navigation : RevealKeys
    data object Features : RevealKeys
}