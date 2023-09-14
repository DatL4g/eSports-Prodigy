package dev.datlag.esports.prodigy.ui.screen.home.dialog.analyzedxvk

import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.ui.dialog.DialogComponent
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AnalyzeDXVKComponent : DialogComponent {

    val dxvkStateCaches: Flow<List<DxvkStateCache>>
    val combineVersionMismatch: Flow<Map<DxvkStateCache, Boolean>>

    fun analyzeFiles(vararg files: File?)
    fun analyzeFiles(files: Collection<File>) = analyzeFiles(*files.toTypedArray())

    fun repairCache(cache: DxvkStateCache)
}