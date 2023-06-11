package dev.datlag.esports.prodigy.model

sealed class ThemeMode(val saveValue: Int) {
    object SYSTEM : ThemeMode(0)
    object LIGHT : ThemeMode(1)
    object DARK : ThemeMode(2)

    companion object {
        fun ofValue(value: Int): ThemeMode {
            return when (value) {
                1 -> LIGHT
                2 -> DARK
                else -> SYSTEM
            }
        }
    }
}