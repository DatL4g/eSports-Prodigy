@file:Suppress("NewApi")

package dev.datlag.esports.prodigy.game

import dev.datlag.esports.prodigy.game.common.decodeFromFile
import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.game.model.LocalGameInfo
import dev.datlag.esports.prodigy.game.model.steam.*
import dev.datlag.esports.prodigy.model.common.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import org.apache.commons.lang3.SystemUtils
import java.io.File

object SteamLauncher {

    const val STEAM_LINUX_ROOT = ".local/share/Steam/"
    const val STEAM_LINUX_SYMLINK_ROOT = ".steam/steam/"
    const val STEAM_LINUX_FLATPAK_ROOT = ".var/app/com.valvesoftware.Steam/.local/share/Steam/"
    const val STEAM_LINUX_FLATPAK_SYMLINK_ROOT = ".var/app/com.valvesoftware.Steam/.steam/steam/"

    const val STEAM_WINDOWS_DEFAULT_ROOT = "C:\\Program Files (x86)\\Steam\\"
    const val STEAM_WINDOWS_NEW_ROOT = "C:\\Program Files\\Steam\\"

    const val STEAM_MAC_DEFAULT_ROOT = "Library/Application Support/Steam/"

    private val steamDirectories by lazy {
        val home = homeDirectory()

        (if (SystemUtils.IS_OS_LINUX) {
            listOf(
                File(home, STEAM_LINUX_ROOT),
                File(home, STEAM_LINUX_SYMLINK_ROOT),
                File(home, STEAM_LINUX_FLATPAK_ROOT),
                File(home, STEAM_LINUX_FLATPAK_SYMLINK_ROOT)
            )
        } else if (SystemUtils.IS_OS_WINDOWS) {
            listOf(
                File(STEAM_WINDOWS_DEFAULT_ROOT),
                File(STEAM_WINDOWS_NEW_ROOT)
            )
        } else if (SystemUtils.IS_OS_MAC) {
            listOf(
                File(home, STEAM_MAC_DEFAULT_ROOT)
            )
        } else {
            listOf(
                File(home, STEAM_LINUX_ROOT),
                File(home, STEAM_LINUX_SYMLINK_ROOT),
                File(home, STEAM_LINUX_FLATPAK_ROOT),
                File(home, STEAM_LINUX_FLATPAK_SYMLINK_ROOT),
                File(STEAM_WINDOWS_DEFAULT_ROOT),
                File(STEAM_WINDOWS_NEW_ROOT),
                File(home, STEAM_MAC_DEFAULT_ROOT)
            )
        }).normalize()
    }

    private fun getSteamAppFolders(list: Collection<File>): List<File> {
        val steamAppsFolder: MutableList<File> = mutableListOf()

        list.forEach {
            steamAppsFolder.add(File(it, "steamapps/"))
            steamAppsFolder.addAll(listOf(
                File(it, "libraryfolders.vdf"),
                File(it, "steamapps/libraryfolders.vdf"),
                File(it, "config/libraryfolders.vdf")
            ).filter { file ->
                file.existsRSafely()
            }.flatMap { file ->
                steamLibraryConfig(file)
            }.map { config ->
                config.path
            }.toSet().map { path ->
                File(path, "steamapps/")
            })
        }

        return steamAppsFolder.normalize()
    }

    private fun steamLibraryConfig(file: File): List<LibraryConfig> {
        val jsonData: JsonElement? = scopeCatching {
            ValveDataFormat.decodeFromFile<JsonElement?>(file)
        }.getOrNull()

        return jsonData?.jsonObject?.values?.mapNotNull {
            val jsonElement = scopeCatching {
                it.jsonObject
            }.getOrNull() ?: it

            scopeCatching {
                ValveDataFormat.json.decodeFromJsonElement<LibraryConfig>(jsonElement)
            }.getOrNull()
        } ?: emptyList()
    }

    private val defaultSteamFolders: MutableStateFlow<List<File>> by lazy {
        MutableStateFlow(steamDirectories)
    }

    private val defaultSteamAppsFolders by lazy {
        defaultSteamFolders.transform {
            return@transform emit(getSteamAppFolders(it))
        }
    }

    private val userSteamFolders: MutableStateFlow<List<File>> by lazy {
        MutableStateFlow(emptyList())
    }

    private val userSteamAppFolders by lazy {
        userSteamFolders.transform {
            return@transform emit(getSteamAppFolders(it))
        }
    }

    val steamFolders by lazy {
        combine(defaultSteamFolders, userSteamFolders) { t1, t2 ->
            listFrom(t1, t2).normalize()
        }
    }

    private val steamAppFolders by lazy {
        combine(defaultSteamAppsFolders, userSteamAppFolders) { t1, t2 ->
            listFrom(t1, t2).normalize()
        }
    }

    val appManifests by lazy {
        steamAppFolders.transform { list ->
            val acfList = list.flatMap { file ->
                file.listFilesSafely().filter {
                    it.extension.equals("acf", true) && it.canReadSafely()
                }
            }
            return@transform emit(coroutineScope {
                acfList.map { async {
                    suspendCatching {
                        ValveDataFormat.decodeFromFile<AppManifest>(it)
                    }.getOrNull()
                } }.awaitAll().filterNotNull()
            })
        }
    }

    val loggedInUsers by lazy {
        steamFolders.transform { list ->
            val userConfigs = list.flatMap { file ->
                listOf(
                    File(file, "config/loginusers.vdf"),
                    File(file, "config/loggedinusers.vdf")
                )
            }.filter { it.canReadSafely() }

            return@transform emit(coroutineScope {
                userConfigs.map { async {
                    suspendCatching {
                        ValveDataFormat.decodeFromFile<Map<String, UserData>>(it)
                    }.getOrNull()
                } }.awaitAll().filterNotNull().flatMap { config ->
                    User.fromMap(config)
                }
            })
        }
    }

    suspend fun asLocalGameInfo(appManifest: AppManifest): LocalGameInfo.Steam {
        val steamAppsFolders = getSteamAppFolders(steamDirectories)

        return LocalGameInfo.Steam(
            appManifest,
            suspendCatching {
                steamAppsFolders.map {
                    File(it, "common/${appManifest.installDir}")
                }.normalize()
            }.getOrNull()?.firstOrNull(),
            steamDirectories.flatMap {
                listOf(
                    File(it, "appcache/librarycache/${appManifest.appId}_header.jpg"),
                    File(it, "appcache/librarycache/${appManifest.appId}_header.jpeg"),
                    File(it, "appcache/librarycache/${appManifest.appId}_header.png")
                )
            }.normalize().firstOrNull(),
            steamDirectories.flatMap {
                listOf(
                    File(it, "appcache/librarycache/${appManifest.appId}_library_hero.jpg"),
                    File(it, "appcache/librarycache/${appManifest.appId}_library_hero.jpeg"),
                    File(it, "appcache/librarycache/${appManifest.appId}_library_hero.png")
                )
            }.normalize().firstOrNull(),
            suspendCatching {
                steamAppsFolders.map {
                    File(it, "shadercache/${appManifest.appId}/DXVK_state_cache")
                }.flatMap { it.listFilesSafely() }.filter {
                    it.extension.equals("dxvk-cache", true)
                }.normalize().mapNotNull {
                    DxvkStateCache.fromFile(it).getOrNull()
                }
            }.getOrNull() ?: emptyList()
        )
    }

}