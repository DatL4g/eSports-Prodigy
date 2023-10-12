package dev.datlag.esports.prodigy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.datastore.core.DataStore
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings
import dev.datlag.esports.prodigy.game.SteamLauncher
import dev.datlag.esports.prodigy.model.common.normalize
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File

@Composable
fun InitSteamLauncher(di: DI) {
    val appSettings: DataStore<AppSettings> by di.instance()
    LaunchedEffect(appSettings) {
        val savedPaths = appSettings.data.map { it.paths.steamList.map { path ->
            File(path)
        }.normalize() }
        SteamLauncher.userSteamFolders.emitAll(savedPaths)
    }
}