@file:Suppress("NewApi")

package dev.datlag.esports.prodigy.game

import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.game.model.legendary.App
import dev.datlag.esports.prodigy.game.model.legendary.AppLibrary
import dev.datlag.esports.prodigy.model.common.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.apache.commons.lang3.SystemUtils
import java.io.File

object HeroicLauncher {

    const val HEROIC_LINUX_FLATPAK_ROOT = ".var/app/com.heroicgameslauncher.hgl/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val heroicDirectories by lazy {
        val home = homeDirectory()

        (if (SystemUtils.IS_OS_LINUX) {
            listOf(
                File(home, HEROIC_LINUX_FLATPAK_ROOT)
            )
        } else {
            emptyList()
        }).normalize()
    }

    private val defaultHeroicAppsFolders: MutableStateFlow<List<File>> by lazy {
        MutableStateFlow(heroicDirectories)
    }

    private val userHeroicAppsFolders: MutableStateFlow<List<File>> by lazy {
        MutableStateFlow(emptyList())
    }

    private val heroicAppFolders by lazy {
        combine(defaultHeroicAppsFolders, userHeroicAppsFolders) { t1, t2 ->
            listFrom(t1, t2).flatMap {
                listOf(
                    File(it, "config/heroic/store_cache"),
                    File(it, "heroic/store_cache")
                )
            }.normalize()
        }
    }

    val apps by lazy {
        heroicAppFolders.transform { list ->
            val libraryList = list.map {
                File(it, "legendary_library.json")
            }.filter { it.existsRSafely() }

            return@transform emit(coroutineScope {
                libraryList.map { async {
                    suspendCatching {
                        it.inputStream().use {
                            json.decodeFromStream<AppLibrary>(it)
                        }
                    }.getOrNull()
                } }.awaitAll().filterNotNull().flatMap { it.library }
            })
        }
    }

    suspend fun asGame(app: App): Game.Heroic {
        return Game.Heroic(
            app,
            app.install.installPath?.let { File(it) },
            suspendCatching {
                app.install.installPath?.let { File(it) }?.walkTopDown()?.filter {
                    it.extension.equals("dxvk-cache", true)
                }?.toList()?.normalize()?.mapNotNull {
                    DxvkStateCache.fromFile(it).getOrNull()
                }
            }.getOrNull() ?: emptyList()
        )
    }
}