package dev.datlag.esports.prodigy.terminal

import androidx.compose.runtime.*
import com.github.rvesse.airline.annotations.Arguments
import com.github.rvesse.airline.annotations.Command
import com.github.rvesse.airline.annotations.Option
import com.github.rvesse.airline.annotations.restrictions.Required
import com.jakewharton.mosaic.layout.background
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.runMosaicBlocking
import com.jakewharton.mosaic.ui.Color.Companion.Black
import com.jakewharton.mosaic.ui.Color.Companion.Green
import com.jakewharton.mosaic.ui.Color.Companion.Red
import com.jakewharton.mosaic.ui.Color.Companion.Yellow
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import dev.datlag.esports.prodigy.game.dxvk.DxvkStateCache
import dev.datlag.esports.prodigy.model.common.canReadSafely
import dev.datlag.esports.prodigy.model.common.normalize
import dev.datlag.esports.prodigy.terminal.restriction.EXISTING_FILES_FLAG_READ
import dev.datlag.esports.prodigy.terminal.restriction.EXISTING_FILES_FLAG_WRITE
import dev.datlag.esports.prodigy.terminal.restriction.ExistingFiles
import java.io.File

@Command(name = "dxvk", description = "Get info of provided dxvk caches and repair them")
class DXVKCommand : CommandLine {

    @Option(name = ["-r", "--repair"], description = "Repair broken dxvk caches")
    private var repair: Boolean = false

    @Arguments(description = "Specifiy your cache files here")
    @Required
    @ExistingFiles(EXISTING_FILES_FLAG_READ)
    private lateinit var caches: List<String>

    override fun run() {
        val validCaches = caches.map {
            File(it)
        }.filter { it.canReadSafely() && it.extension.equals("dxvk-cache", true) }.normalize()

        if (validCaches.isEmpty()) {
            return
        } else {
            runMosaicBlocking {
                setContent {
                    Text(
                        value = "Loading DXVK Information",
                        style = TextStyle.Bold,
                        modifier = Modifier.padding(vertical = 2)
                    )

                    Column {
                        validCaches.forEach {
                            CacheRow(it)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CacheRow(cacheFile: File) {
        var state by remember { mutableStateOf<State>(State.LOADING) }
        val dir = cacheFile.path.substringBeforeLast('/', ".")
        val name = cacheFile.path.substringAfterLast('/')

        LaunchedEffect(cacheFile) {
            val result = DxvkStateCache.fromFile(cacheFile).getOrNull()

            val repaired = if (repair) {
                result?.repair()?.getOrNull() ?: result
            } else {
                result
            }
            state = repaired?.let { State.DONE(it) } ?: State.ERROR
        }

        Column {
            Row {
                val bg = when (state) {
                    is State.LOADING -> Yellow
                    is State.DONE -> Green
                    is State.ERROR -> Red
                }
                val stateText = when (state) {
                    is State.LOADING -> "LOADING"
                    is State.DONE -> "DONE"
                    is State.ERROR -> "ERROR"
                }

                Text(
                    value = stateText,
                    modifier = Modifier.background(bg).padding(horizontal = 1),
                    color = Black
                )
                Text(modifier = Modifier.padding(left = 1), value = "$dir/")
                Text(value = name, style = TextStyle.Bold)
            }
            (state as? State.DONE)?.cache?.let { CacheInfo(it) }
        }
    }

    @Composable
    fun CacheInfo(cache: DxvkStateCache) {
        Column(modifier = Modifier.padding(bottom = 1)) {
            Row {
                Text(
                    value = " ‣ Version:",
                    modifier = Modifier.padding(right = 1)
                )
                Text(
                    value = "${cache.header.version}",
                    style = TextStyle.Bold
                )
            }
            Row {
                Text(
                    value = " ‣ Entries:",
                    modifier = Modifier.padding(right = 1)
                )
                Text(
                    value = "${cache.entries.size + cache.invalidEntries}",
                    style = TextStyle.Bold
                )
            }
            Row {
                val color = if (cache.invalidEntries <= 0) {
                    null
                } else {
                    Red
                }

                Text(
                    value = " ‣ Invalid Entries:",
                    modifier = Modifier.padding(right = 1),
                    color = color
                )
                Text(
                    value = cache.invalidEntries.toString(),
                    style = TextStyle.Bold,
                    color = color
                )
            }
        }
    }
}

private sealed interface State {
    data object LOADING : State
    data class DONE(val cache: DxvkStateCache) : State
    data object ERROR : State
}