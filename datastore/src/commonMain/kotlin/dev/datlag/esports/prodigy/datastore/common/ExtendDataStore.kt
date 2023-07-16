package dev.datlag.esports.prodigy.datastore.common

import androidx.datastore.core.DataStore
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings

suspend fun DataStore<AppSettings>.updateAppearance(
    themeMode: Int? = null
): AppSettings {
    return this.updateData {
        it.toBuilder().setAppearance(
            it.appearance.toBuilder().setThemeMode(
                themeMode ?: it.appearance.themeMode
            ).build()
        ).build()
    }
}

suspend fun DataStore<AppSettings>.updatePaths(
    steam: List<String>? = null
): AppSettings {
    return this.updateData {
        val steamSafe = steam ?: it.paths.steamList

        it.toBuilder().setPaths(
            it.paths.toBuilder().clearSteam().addAllSteam(steamSafe).build()
        ).build()
    }
}