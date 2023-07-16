package dev.datlag.esports.prodigy.ui.screen.settings.dialog

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.common.*
import dev.datlag.esports.prodigy.datastore.common.updatePaths
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings
import dev.datlag.esports.prodigy.game.SteamLauncher
import dev.datlag.esports.prodigy.model.common.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File

class SteamFinderDialogComponent(
    componentContext: ComponentContext,
    private val onDismissed: () -> Unit,
    override val di: DI
) : SteamFinderComponent, ComponentContext by componentContext {

    private val scope = mainScope()

    private val appSettings: DataStore<AppSettings> by di.instance()
    private val appSettingsFile: File by di.instance("AppSettingsFile")
    private val appSettingsTempFile = appSettingsFile.suffix(".tmp")

    private val _currentSearchDir: MutableStateFlow<File?> = MutableStateFlow(null)
    override val currentSearchDir: Flow<File> = _currentSearchDir.mapNotNull { it }

    private var runningJob: Job? = null
    private val _searchState: MutableStateFlow<SteamFinderComponent.State?> = MutableStateFlow(null)
    override val searchState: Flow<SteamFinderComponent.State> = _searchState.mapNotNull { it }

    override val settingsExisting = appSettings.data.map { it.paths.steamList.map { path ->
        File(path)
    }.normalize() }

    override val managedSteamDirs = SteamLauncher.defaultSteamFolders

    private val _unmanagedSteamDirs: MutableStateFlow<List<File>> = MutableStateFlow(settingsExisting.getValueBlocking(emptyList()))
    override val unmanagedSteamDirs: Flow<List<File>> = _unmanagedSteamDirs

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
                            if (managedSteamDirs.value.any { e -> step.isSame(e) }) {
                                null
                            } else {
                                _unmanagedSteamDirs.safeEmit(listFrom(_unmanagedSteamDirs.value, listOf(step)).normalize(), this)
                                step
                            }
                        } else {
                            null
                        }
                    }.toList()
                } }.awaitAll().flatten().normalize()

                val managed = managedSteamDirs.firstOrNull()?.ifEmpty { null } ?: managedSteamDirs.value
                val unmanaged = listFrom(found, settingsExisting.firstOrNull() ?: emptyList()).toMutableList().apply {
                    removeAll {
                        managed.any { e -> e.isSame(it) }
                    }
                    addAll(
                        settingsExisting.firstOrNull() ?: emptyList()
                    )
                }.toSet().normalize()

                _unmanagedSteamDirs.emit(unmanaged)

                if (unmanaged.isEmpty()) {
                    _searchState.emit(SteamFinderComponent.State.EXISTING)
                } else {
                    _searchState.emit(SteamFinderComponent.State.FINISHED)
                }
            }
        }
    }

    override fun save() {
        scope.launchIO {
            appSettingsTempFile.deleteSafely()
            appSettings.updatePaths(
                steam = _unmanagedSteamDirs.value.mapNotNull { it.absolutePath }
            )
            withMainContext {
                onDismissed()
            }
        }
    }

    override fun deleteItem(item: File) {
        scope.launchIO {
            _unmanagedSteamDirs.emit(_unmanagedSteamDirs.value.toMutableList().apply {
                remove(item)
            })
            _searchState.emit(SteamFinderComponent.State.FINISHED)
        }
    }
}