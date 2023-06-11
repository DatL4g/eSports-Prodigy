package dev.datlag.esports.prodigy.game.model

import dev.datlag.esports.prodigy.model.common.listFrom
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import java.io.File

data class LocalGame(
    val name: String,
    val steam: LocalGameInfo.Steam?,
    val heroic: LocalGameInfo.Heroic?
) {

    val dxvkCaches = combine(
        steam?.dxvkCaches ?: flowOf(emptyList()),
        heroic?.dxvkCaches ?: flowOf(emptyList())
    ) { steamList, heroicList ->
        mapOf(
            LocalGameInfo.TYPE.STEAM to steamList,
            LocalGameInfo.TYPE.HEROIC to heroicList
        )
    }

    val headerUrl: String = steam?.headerUrl ?: heroic?.artCover!!
    val heroUrl: String = steam?.heroUrl ?: heroic?.artCover!!

    val headerFile: File? = steam?.headerFile
    val heroFile: File? = steam?.heroFile

    val steamDirectory: File? = steam?.directory
    val heroicDirectory: File? = heroic?.directory

    val directories: Map<LocalGameInfo.TYPE, File?> = mapOf(
        LocalGameInfo.TYPE.STEAM to steamDirectory,
        LocalGameInfo.TYPE.HEROIC to heroicDirectory
    )

    val games: List<LocalGameInfo> = listOfNotNull(steam, heroic)

    suspend fun reloadDxvkCaches() {
        steam?.reloadDxvkCaches()
        heroic?.reloadDxvkCaches()
    }

    companion object {

        private data class GameInfo(
            var steam: LocalGameInfo.Steam? = null,
            var heroic: LocalGameInfo.Heroic? = null
        )

        fun combineGames(list: Collection<LocalGameInfo>): List<LocalGame> {
            val foundGames = mutableMapOf<String, GameInfo>()

            list.forEach { game ->
                val matchingKey = foundGames.filterKeys {
                    it.equals(game.name, true) || it.filter { char ->
                        char.isLetterOrDigit()
                    }.equals(game.name.filter { char ->
                        char.isLetterOrDigit()
                    }, true)
                }.keys.firstOrNull()

                if (matchingKey != null && foundGames.containsKey(matchingKey)) {
                    when (game) {
                        is LocalGameInfo.Steam -> {
                            foundGames[matchingKey]!!.steam = game
                        }
                        is LocalGameInfo.Heroic -> {
                            foundGames[matchingKey]!!.heroic = game
                        }
                    }

                } else {
                    foundGames[game.name] = when (game) {
                        is LocalGameInfo.Steam -> GameInfo(
                            steam = game
                        )
                        is LocalGameInfo.Heroic -> GameInfo(
                            heroic = game
                        )
                    }
                }
            }

            return foundGames.map { (name, info) ->
                LocalGame(
                    name = name,
                    steam = info.steam,
                    heroic = info.heroic
                )
            }
        }
    }
}
