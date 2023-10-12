package dev.datlag.esports.prodigy

import androidx.compose.runtime.*
import com.multiplatform.webview.web.Cef
import dev.datlag.esports.prodigy.common.withIOContext
import dev.datlag.esports.prodigy.ui.LocalRestartRequired
import dev.datlag.esports.prodigy.ui.browser.ApplicationDisposer
import java.io.File

val LocalCEFInitialization = staticCompositionLocalOf<MutableState<CEFState>> { error("No CEFInitialization state provided") }

@Composable
fun InitCEF(content: @Composable () -> Unit) {
    val restartRequiredInitial = LocalRestartRequired.current
    var restartRequired by remember { mutableStateOf(restartRequiredInitial) }
    val cefState = mutableStateOf<CEFState>(CEFState.LOCATING)

    LaunchedEffect(ApplicationDisposer.current) {
        withIOContext {
            Cef.init(builder = {
                installDir = File(AppIO.getWriteableExecutableFolder(), "jcef-bundle")
                settings {
                    logSeverity = Cef.Settings.LogSeverity.Disable
                }
            }, initProgress = {
                onLocating {
                    cefState.value = CEFState.LOCATING
                }
                onDownloading {
                    cefState.value = CEFState.Downloading(it)
                }
                onExtracting {
                    cefState.value = CEFState.EXTRACTING
                }
                onInstall {
                    cefState.value = CEFState.INSTALLING
                }
                onInitializing {
                    cefState.value = CEFState.INITIALIZING
                }
                onInitialized {
                    cefState.value = CEFState.INITIALIZED
                }
            }, onRestartRequired = {
                restartRequired = true
            })
        }
    }

    CompositionLocalProvider(
        LocalRestartRequired provides restartRequired,
        LocalCEFInitialization provides cefState
    ) {
        content()
    }
}

sealed interface CEFState {
    data object LOCATING : CEFState
    data class Downloading(val progress: Float) : CEFState
    data object EXTRACTING : CEFState
    data object INSTALLING : CEFState
    data object INITIALIZING : CEFState
    data object INITIALIZED : CEFState
}