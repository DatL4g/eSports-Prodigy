package dev.datlag.esports.prodigy

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.multiplatform.webview.web.Cef
import dev.datlag.esports.prodigy.common.basedOnSize
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle
import dev.datlag.esports.prodigy.common.withIOContext
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings
import dev.datlag.esports.prodigy.game.SteamLauncher
import dev.datlag.esports.prodigy.model.common.normalize
import dev.datlag.esports.prodigy.module.NetworkModule
import dev.datlag.esports.prodigy.other.Commonizer
import dev.datlag.esports.prodigy.terminal.CLI
import dev.datlag.esports.prodigy.ui.*
import dev.datlag.esports.prodigy.ui.browser.ApplicationDisposer
import dev.datlag.esports.prodigy.ui.navigation.NavHostComponent
import dev.datlag.sekret.Sekret
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.kamel.core.config.*
import io.kamel.image.config.*
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File

@OptIn(ExperimentalDecomposeApi::class, ExperimentalComposeUiApi::class)
fun main(vararg args: String) {
    if (args.isEmpty()) {
        runWindow()
    } else {
        parseCmd(args.toList())
    }
}

private fun parseCmd(args: Collection<String>) {
    CLI.initAndRun(args) {
        println("Could not parse commandline arguments, launching UI.")
        runWindow()
    }
}

@OptIn(ExperimentalDecomposeApi::class)
private fun runWindow() {
    val appTitle = StringDesc.Resource(SharedRes.strings.app_name).localized()
    AppIO.applyTitle(appTitle)

    val windowState = WindowState()
    val lifecycle = LifecycleRegistry()
    val lifecycleOwner = object : LifecycleOwner {
        override val lifecycle: Lifecycle = lifecycle
    }
    val di = DI {
        import(NetworkModule.di)
    }

    val root = NavHostComponent.create(DefaultComponentContext(lifecycle), di)
    val imageConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        resourcesFetcher()
        batikSvgDecoder()
    }
    Napier.base(DebugAntilog())

    disposableSingleWindowApplication(
        state = windowState,
        title = appTitle,
        onKeyEvent = {
            if (it.type == KeyEventType.KeyUp) {
                when (it.key) {
                    Key.F11 -> {
                        windowState.placement = if (windowState.placement == WindowPlacement.Fullscreen) {
                            WindowPlacement.Floating
                        } else {
                            WindowPlacement.Fullscreen
                        }
                    }
                }
            }
            true
        },
        exitProcessOnExit = true
    ) {
        LifecycleController(lifecycle, windowState)

        AppIO.loadAppIcon(
            this.window,
            rememberCoroutineScope(),
            SharedRes.assets.icns.launcher,
            SharedRes.assets.ico.launcher_192,
            SharedRes.assets.ico.launcher_128,
            SharedRes.assets.ico.launcher_96,
            SharedRes.assets.ico.launcher_64,
            SharedRes.assets.ico.launcher_48,
            SharedRes.assets.ico.launcher_32,
            SharedRes.assets.ico.launcher_24,
            SharedRes.assets.ico.launcher_16,
            SharedRes.assets.png.launcher_1024,
            SharedRes.assets.png.launcher_768,
            SharedRes.assets.png.launcher_512,
            SharedRes.assets.png.launcher_384,
            SharedRes.assets.png.launcher_256,
            SharedRes.assets.png.launcher_192,
            SharedRes.assets.png.launcher_128,
            SharedRes.assets.png.launcher_96,
            SharedRes.assets.png.launcher_64,
            SharedRes.assets.png.launcher_48,
            SharedRes.assets.png.launcher_32,
            SharedRes.assets.png.launcher_24,
            SharedRes.assets.png.launcher_16
        )

        val appSettings: DataStore<AppSettings> by di.instance()
        LaunchedEffect(appSettings) {
            val savedPaths = appSettings.data.map { it.paths.steamList.map { path ->
                File(path)
            }.normalize() }
            SteamLauncher.userSteamFolders.emitAll(savedPaths)
        }

        val celebrity by SteamLauncher.loggedInUsers.mapNotNull { it.firstNotNullOfOrNull { u -> u.celebrity } }.collectAsStateWithLifecycle(initialValue = null)
        val restartRequiredInitial = LocalRestartRequired.current
        var restartRequired by remember { mutableStateOf(restartRequiredInitial) }

        LaunchedEffect(ApplicationDisposer.current) {
            withIOContext {
                Cef.init(builder = {
                    installDir = File(AppIO.getWriteableExecutableFolder(), "jcef-bundle")
                }, initProgress = {
                }, onRestartRequired = {
                    restartRequired = true
                })
            }
        }

        Sekret().talkBack()?.let { Napier.i(it) } ?: Napier.e("Sekret was not loaded")

        CompositionLocalProvider(
            LocalOrientation provides Orientation.basedOnSize(windowState),
            LocalKamelConfig provides imageConfig,
            LocalCommonizer provides Commonizer(ApplicationDisposer.current),
            LocalCelebrity provides celebrity,
            LocalWindow provides this.window,
            LocalLifecycleOwner provides lifecycleOwner,
            LocalRestartRequired provides restartRequired
        ) {
            App(di) {
                root.render()
            }
        }
    }
}