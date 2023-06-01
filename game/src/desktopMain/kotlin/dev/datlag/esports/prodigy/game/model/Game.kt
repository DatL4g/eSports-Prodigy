package dev.datlag.esports.prodigy.game.model

import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.game.model.steam.AppManifest
import java.io.File

sealed class Game(
    open val name: String,
    open val directory: File?,
    open val headerUrl: String?,
    open val heroUrl: String?,
    open val dxvkCaches: List<DxvkStateCache>
) {

    data class Steam(
        val manifest: AppManifest,
        override val directory: File?,
        val headerFile: File?,
        val heroFile: File?,
        override val dxvkCaches: List<DxvkStateCache>
    ) : Game(
        manifest.name,
        directory,
        buildHeaderUrl(manifest.appId),
        buildHeroUrl(manifest.appId),
        dxvkCaches
    ) {

        companion object {
            fun buildHeaderUrl(appId: String) = "https://cdn.akamai.steamstatic.com/steam/apps/$appId/header.jpg"
            fun buildHeroUrl(appId: String) = "https://cdn.akamai.steamstatic.com/steam/apps/$appId/library_hero.jpg"
        }
    }
}