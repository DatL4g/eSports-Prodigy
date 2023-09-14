package dev.datlag.esports.prodigy.ui.screen.home.dialog.analyzedxvk

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.common.ioScope
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.game.dxvk.DXVKException
import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.model.common.canReadSafely
import dev.datlag.esports.prodigy.model.common.isSame
import dev.datlag.esports.prodigy.model.common.move
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.kodein.di.DI
import java.io.File

class AnalyzeDXVKDialogComponent(
    componentContext: ComponentContext,
    private val onDismissed: () -> Unit,
    override val di: DI
) : AnalyzeDXVKComponent, ComponentContext by componentContext {

    private val scope = ioScope()
    private var runningJob: Job? = null

    private val _dxvkStateCaches: MutableStateFlow<List<DxvkStateCache>> = MutableStateFlow(emptyList())
    override val dxvkStateCaches: Flow<List<DxvkStateCache>> = _dxvkStateCaches

    override val combineVersionMismatch: MutableStateFlow<Map<DxvkStateCache, Boolean>> = MutableStateFlow(mutableMapOf())

    @Composable
    override fun render() {
        AnalyzeDXVKDialog(this)
    }

    override fun dismiss() {
        runningJob?.cancel()
        onDismissed()
    }

    override fun analyzeFiles(vararg files: File?) {
        val validFiles = files.mapNotNull {
            if (it?.canReadSafely() == true && it.extension.equals("dxvk-cache", true)) {
                it
            } else {
                null
            }
        }

        runningJob?.cancel()
        if (validFiles.isEmpty()) {
            scope.launchIO {
                _dxvkStateCaches.emit(emptyList())
            }
        } else {
            runningJob = scope.launchIO {
                val read = validFiles.map { async {
                    DxvkStateCache.fromFile(it).getOrNull()
                } }.awaitAll().filterNotNull()

                _dxvkStateCaches.emit(read)
            }
        }
    }

    override fun repairCache(cache: DxvkStateCache) {
        scope.launchIO {
            val originalName = cache.file.name
            val backupFile = cache.file.move("$originalName.bak")

            val loadBackupFile = cache.writeToFile(cache.file).isFailure
            if (loadBackupFile) {
                backupFile.move(originalName)
            } else {
                val currentList = _dxvkStateCaches.value.toMutableList()

                val replaceIndex = currentList.indexOfFirst {
                    it.file.canonicalPath == cache.file.canonicalPath
                }
                if (replaceIndex >= 0) {
                    _dxvkStateCaches.emit(currentList.apply {
                        set(replaceIndex, DxvkStateCache.fromFile(cache.file).getOrNull() ?: cache)
                    })
                }
            }
        }
    }
}