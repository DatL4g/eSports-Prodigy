package dev.datlag.esports.prodigy

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import dev.datlag.esports.prodigy.common.basedOnSize
import dev.datlag.esports.prodigy.common.basedOnWidth
import dev.datlag.esports.prodigy.module.DataStoreModule
import dev.datlag.esports.prodigy.module.NetworkModule
import dev.datlag.esports.prodigy.ui.*
import dev.datlag.esports.prodigy.ui.navigation.NavHostComponent
import dev.datlag.sekret.Sekret
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.*
import org.kodein.di.DI
import java.io.File

@OptIn(ExperimentalDecomposeApi::class, ExperimentalComposeUiApi::class)
fun main() {
    val appTitle = StringDesc.Resource(SharedRes.strings.app_name).localized()
    AppIO.applyTitle(appTitle)

    val windowState = WindowState()
    val lifecycle = LifecycleRegistry()
    val di = DI {
        import(NetworkModule.di)
    }

    val root = NavHostComponent.create(DefaultComponentContext(lifecycle), di)
    val imageConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        resourcesFetcher()
        svgDecoder()
        imageVectorDecoder()
    }
    Napier.base(DebugAntilog())

    singleWindowApplication(
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
        }
    ) {
        LifecycleController(lifecycle, windowState)

        AppIO.loadAppIcon(
            this.window,
            rememberCoroutineScope(),
            SharedRes.assets.icns.launcher,
            SharedRes.assets.ico.launcher_128,
            SharedRes.assets.ico.launcher_96,
            SharedRes.assets.ico.launcher_64,
            SharedRes.assets.ico.launcher_48,
            SharedRes.assets.ico.launcher_32,
            SharedRes.assets.ico.launcher_16,
            SharedRes.assets.png.launcher_128,
            SharedRes.assets.png.launcher_96,
            SharedRes.assets.png.launcher_64,
            SharedRes.assets.png.launcher_48,
            SharedRes.assets.png.launcher_32,
            SharedRes.assets.png.launcher_16,
            SharedRes.assets.svg.launcher_128,
            SharedRes.assets.svg.launcher_96,
            SharedRes.assets.svg.launcher_64,
            SharedRes.assets.svg.launcher_48,
            SharedRes.assets.svg.launcher_32,
            SharedRes.assets.svg.launcher_16
        )

        CompositionLocalProvider(
            LocalWindowSize provides WindowSize.basedOnWidth(windowState),
            LocalOrientation provides Orientation.basedOnSize(windowState),
            LocalKamelConfig provides imageConfig
        ) {
            App(di) {
                root.render()
            }
        }
    }
}