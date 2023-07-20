package dev.datlag.esports.prodigy.datastore.common

import androidx.datastore.core.DataStore
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings

suspend fun DataStore<AppSettings>.updateAppearance(
    themeMode: Int? = null,
    contentColors: Boolean? = null,
): AppSettings {
    return this.updateData {
        it.toBuilder().setAppearance(
            it.appearance.toBuilder().setThemeMode(
                themeMode ?: it.appearance.themeMode
            ).setContentColors(
                contentColors ?: it.appearance.contentColors
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

suspend fun DataStore<AppSettings>.updateWelcomed(
    done: Boolean
): AppSettings {
    return this.updateData {
        it.toBuilder().setWelcomed(done).build()
    }
}

suspend fun DataStore<AppSettings>.updateCommented(
    done: Boolean
): AppSettings {
    return this.updateData {
        it.toBuilder().setCommented(done).build()
    }
}