package dev.datlag.esports.prodigy.ui.screen.settings.dialog

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.common.mainScope
import dev.datlag.esports.prodigy.common.safeEmit
import dev.datlag.esports.prodigy.game.SteamLauncher
import dev.datlag.esports.prodigy.model.common.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import org.kodein.di.DI
import java.io.File

class SteamFinderDialogComponent(
    componentContext: ComponentContext,
    private val onDismissed: () -> Unit,
    override val di: DI
) : SteamFinderComponent, ComponentContext by componentContext {

    private val scope = mainScope()

    private val _foundSteamDirs: MutableStateFlow<List<File>> = MutableStateFlow(emptyList())
    override val foundSteamDirs: Flow<List<File>> = _foundSteamDirs

    private val _currentSearchDir: MutableStateFlow<File?> = MutableStateFlow(null)
    override val currentSearchDir: Flow<File> = _currentSearchDir.mapNotNull { it }

    private var runningJob: Job? = null
    private val _searchState: MutableStateFlow<SteamFinderComponent.State?> = MutableStateFlow(null)
    override val searchState: Flow<SteamFinderComponent.State> = _searchState.mapNotNull { it }

    override var existingSteamDirs = SteamLauncher.steamFolders

    @Composable
    override fun render() {
        SteamFinderDialog(this)
    }

    override fun dismiss() {
        runningJob?.cancel()
        onDismissed()
    }

    override fun search() {
        val job = runningJob
        if (job == null || !job.isActive) {
            runningJob = scope.launchIO {
                _searchState.emit(SteamFinderComponent.State.RUNNING)
                val found = findSystemRoots().map { async {
                    it.walkTopDown().onEnter { step ->
                        step.isDirectorySafely() && step.canReadSafely() && !step.isSymlinkSafely()
                    }.mapNotNull { step ->
                        _currentSearchDir.safeEmit(step, this)

                        if (File(step, "steamapps").existsRSafely()
                            && File(step, "config").existsRSafely()
                            && File(step, "userdata").existsRSafely()) {
                            _foundSteamDirs.safeEmit(listFrom(_foundSteamDirs.value, listOf(step)).normalize(), this)
                            step
                        } else {
                            null
                        }
                    }.toList()
                } }.awaitAll().flatten().normalize()

                _foundSteamDirs.emit(found)

                val existing = existingSteamDirs.firstOrNull() ?: emptyList()
                if (_foundSteamDirs.value.all { existing.any { e -> e.isSame(it) } }) {
                    _searchState.emit(SteamFinderComponent.State.EXISTING)
                } else {
                    _searchState.emit(SteamFinderComponent.State.FINISHED)
                }
            }
        }
    }
}