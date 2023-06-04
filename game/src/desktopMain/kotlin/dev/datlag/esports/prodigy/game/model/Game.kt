package dev.datlag.esports.prodigy.game.model

import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.game.model.legendary.App
import dev.datlag.esports.prodigy.game.model.steam.AppManifest
import java.io.File

sealed class Game(
    open val name: String,
    open val directories: Map<TYPE, File?>,
    open val headerUrl: String?,
    open val heroUrl: String?,
    open val dxvkCaches: List<DxvkStateCache>
) {

    abstract val type: TYPE?

    data class Steam(
        val manifest: AppManifest,
        override val directories: Map<TYPE, File?>,
        val headerFile: File?,
        val heroFile: File?,
        override val dxvkCaches: List<DxvkStateCache>
    ) : Game(
        name = manifest.name,
        directories = directories,
        headerUrl = buildHeaderUrl(manifest.appId),
        heroUrl = buildHeroUrl(manifest.appId),
        dxvkCaches = dxvkCaches
    ) {

        constructor(
            manifest: AppManifest,
            directory: File?,
            headerFile: File?,
            heroFile: File?,
            dxvkCaches: List<DxvkStateCache>
        ) : this(
            manifest,
            mapOf(TYPE.STEAM to directory),
            headerFile,
            heroFile,
            dxvkCaches
        )

        override val type: TYPE = TYPE.STEAM

        companion object {
            fun buildHeaderUrl(appId: String) = "https://cdn.akamai.steamstatic.com/steam/apps/$appId/header.jpg"
            fun buildHeroUrl(appId: String) = "https://cdn.akamai.steamstatic.com/steam/apps/$appId/library_hero.jpg"
        }
    }

    data class Heroic(
        val app: App,
        override val directories: Map<TYPE, File?>,
        override val dxvkCaches: List<DxvkStateCache>
    ): Game(
        name = app.title,
        directories = directories,
        headerUrl = app.artCover,
        heroUrl = app.artCover,
        dxvkCaches = dxvkCaches
    ) {

        constructor(
            app: App,
            directory: File?,
            dxvkCaches: List<DxvkStateCache>
        ) : this(
            app,
            mapOf(TYPE.HEROIC to directory),
            dxvkCaches
        )

        override val type: TYPE = TYPE.HEROIC
    }

    data class Multi private constructor(
        val games: List<Game>,
        private val sortedGames: List<Game> = games.sortedBy { it !is Steam }
    ) : Game(
        name = sortedGames.first().name,
        directories = sortedGames.associate {
            val type = if (it is Steam) {
                TYPE.STEAM
            } else {
                TYPE.HEROIC
            }

            type to it.directories[type]
        },
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

        val steam = games.firstNotNullOfOrNull {
            if (it is Steam) {
                it
            } else {
                null
            }
        }

        val heroic = games.firstNotNullOfOrNull {
            if (it is Heroic) {
                it
            } else {
                null
            }
        }

        val hasSteam: Boolean = steam != null
        val hasHeroic: Boolean = heroic != null

        override val type: TYPE? = null
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

    sealed interface TYPE {
        object STEAM : TYPE
        object HEROIC : TYPE
    }
}