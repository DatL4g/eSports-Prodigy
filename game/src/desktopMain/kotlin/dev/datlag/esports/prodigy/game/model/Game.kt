package dev.datlag.esports.prodigy.game.model

import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.game.model.legendary.App
import dev.datlag.esports.prodigy.game.model.steam.AppManifest
import dev.datlag.esports.prodigy.model.common.listFrom
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
        name = manifest.name,
        directory = directory,
        headerUrl = buildHeaderUrl(manifest.appId),
        heroUrl = buildHeroUrl(manifest.appId),
        dxvkCaches = dxvkCaches
    ) {

        companion object {
            fun buildHeaderUrl(appId: String) = "https://cdn.akamai.steamstatic.com/steam/apps/$appId/header.jpg"
            fun buildHeroUrl(appId: String) = "https://cdn.akamai.steamstatic.com/steam/apps/$appId/library_hero.jpg"
        }
    }

    data class Heroic(
        val app: App,
        override val directory: File?,
        override val dxvkCaches: List<DxvkStateCache>
    ): Game(
        name = app.title,
        directory = directory,
        headerUrl = app.artCover,
        heroUrl = app.artCover,
        dxvkCaches = dxvkCaches
    )

    data class Multi private constructor(
        private val games: List<Game>,
        private val sortedGames: List<Game> = games.sortedBy { it !is Steam }
    ) : Game(
        name = sortedGames.first().name,
        directory = sortedGames.firstNotNullOfOrNull { it.directory },
        headerUrl = sortedGames.first().headerUrl,
        heroUrl = sortedGames.firstNotNullOfOrNull {
            if (it.heroUrl != sortedGames.first().headerUrl) {
                it.heroUrl
            } else {
                null
            }
        } ?: sortedGames.first().heroUrl,
        dxvkCaches = games.flatMap { it.dxvkCaches }
    ) {

        constructor(gameList: List<Game>) : this(games = gameList)

        constructor(vararg gameArray: Game) : this(games = gameArray.toList())

        val headerFile = sortedGames.firstNotNullOfOrNull {
            if (it is Steam) {
                it.headerFile
            } else {
                null
            }
        }

        val heroFile = sortedGames.firstNotNullOfOrNull {
            if (it is Steam) {
                it.heroFile
            } else {
                null
            }
        }
    }

    companion object {
        fun flatten(games: List<Game>): List<Game> {
            val foundGames = mutableMapOf<String, MutableList<Game>>()

            games.forEach { game ->
                val matchingKey = foundGames.filterKeys {
                    it.equals(game.name, true) || it.filter { char ->
                        char.isLetterOrDigit()
                    }.equals(game.name.filter { char ->
                        char.isLetterOrDigit()
                    }, true)
                }.keys.firstOrNull()

                if (matchingKey != null) {
                    foundGames[matchingKey]!!.add(game)
                } else {
                    foundGames[game.name] = mutableListOf(game)
                }
            }

            return foundGames.values.mapNotNull { list ->
                if (list.size > 1) {
                    Multi(list)
                } else {
                    list.firstOrNull()
                }
            }
        }
    }
}