package dev.datlag.esports.prodigy.ui.screen.settings.dialog

import dev.datlag.esports.prodigy.ui.dialog.DialogComponent
import kotlinx.coroutines.flow.Flow
import java.io.File

interface SteamFinderComponent : DialogComponent {
    val managedSteamDirs: Flow<List<File>>
    val unmanagedSteamDirs: Flow<List<File>>
    val settingsExisting: Flow<List<File>>
    val currentSearchDir: Flow<File>
    val searchState: Flow<State>

    fun search()

    fun save()

    fun deleteItem(item: File)

    sealed interface State {
        object RUNNING : State
        object FINISHED : State
        object EXISTING : State
    }
}