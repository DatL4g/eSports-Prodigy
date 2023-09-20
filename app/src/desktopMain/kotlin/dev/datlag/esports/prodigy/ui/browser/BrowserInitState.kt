package dev.datlag.esports.prodigy.ui.browser

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.friwi.jcefmaven.EnumProgress
import me.friwi.jcefmaven.IProgressHandler

@Stable
interface BrowserInitState {
    val step: EnumProgress
    val progress: Float
}

@Stable
class BrowserInitStateImpl : BrowserInitState, IProgressHandler {

    override var progress: Float by mutableStateOf(EnumProgress.NO_ESTIMATION)
        private set

    override var step: EnumProgress by mutableStateOf(EnumProgress.LOCATING)
        private set

    override fun handleProgress(state: EnumProgress, percent: Float) {
        progress = progress
        step = state
    }
}