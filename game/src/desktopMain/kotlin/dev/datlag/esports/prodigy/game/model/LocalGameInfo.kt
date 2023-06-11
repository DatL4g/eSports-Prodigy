package dev.datlag.esports.prodigy.game.model

import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.game.model.legendary.App
import dev.datlag.esports.prodigy.game.model.steam.AppManifest
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

sealed class LocalGameInfo(
    open val name: String
) {

    abstract val dxvkCaches: MutableStateFlow<List<DxvkStateCache>>
    abstract val type: TYPE

    suspend fun reloadDxvkCaches() {
        dxvkCaches.emit(dxvkCaches.value.map { it.file }.mapNotNull {
            DxvkStateCache.fromFile(it).getOrNull()
        }.ifEmpty { dxvkCaches.value })
    }

    data class Steam(
        val manifest: AppManifest,
        val directory: File?,
        val headerFile: File?,
        val heroFile: File?,
        private val _dxvkCaches: List<DxvkStateCache>
    ) : LocalGameInfo(manifest.name) {

        override val dxvkCaches = MutableStateFlow(_dxvkCaches)
        override val type: TYPE = TYPE.STEAM
        val headerUrl: String = buildHeaderUrl(manifest.appId)
        val heroUrl: String = buildHeroUrl(manifest.appId)

        companion object {
            fun buildHeaderUrl(appId: String) = "https://cdn.akamai.steamstatic.com/steam/apps/$appId/header.jpg"
            fun buildHeroUrl(appId: String) = "https://cdn.akamai.steamstatic.com/steam/apps/$appId/library_hero.jpg"
        }
    }

    data class Heroic(
        val app: App,
        val directory: File?,
        private val _dxvkCaches: List<DxvkStateCache>
    ) : LocalGameInfo(app.title) {

        override val dxvkCaches = MutableStateFlow(_dxvkCaches)
        override val type: TYPE = TYPE.HEROIC
        val artCover: String = app.artCover
    }

    sealed interface TYPE {
        object STEAM : TYPE
        object HEROIC : TYPE
    }
}