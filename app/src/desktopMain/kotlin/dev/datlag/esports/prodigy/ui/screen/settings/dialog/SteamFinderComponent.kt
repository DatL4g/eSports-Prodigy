package dev.datlag.esports.prodigy.ui.screen.settings.dialog

import dev.datlag.esports.prodigy.ui.dialog.DialogComponent
import kotlinx.coroutines.flow.Flow
import java.io.File

interface SteamFinderComponent : DialogComponent {
    val foundSteamDirs: Flow<List<File>>
    val currentSearchDir: Flow<File>
    val searchState: Flow<State>
    val existingSteamDirs: Flow<List<File>>

    fun search()

    sealed interface State {
        object RUNNING : State
        object FINISHED : State
        object EXISTING : State
    }
}