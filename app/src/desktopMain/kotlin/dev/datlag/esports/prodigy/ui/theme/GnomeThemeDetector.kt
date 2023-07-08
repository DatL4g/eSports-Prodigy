package dev.datlag.esports.prodigy.ui.theme

import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.other.Constants
import evalBash
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.isActive
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(DelicateCoroutinesApi::class)
object GnomeThemeDetector : ThemeDetector {

    override val isDark: Boolean
        get() {
            return (Constants.LINUX_DARK_MODE_CMD.evalBash(env = null).getOrDefault(String())).ifEmpty {
                Constants.LINUX_DARK_MODE_LEGACY_CMD.evalBash(env = null).getOrDefault(String())
            }.contains("dark", true)
        }

    private var listener: (isDark: Boolean) -> Unit = { }

    init {
        val runtime = Runtime.getRuntime()

        GlobalScope.launchIO {
            val monitorProcess = runtime.exec(Constants.LINUX_THEME_MONITOR_CMD)
            try {
                val reader = BufferedReader(InputStreamReader(monitorProcess.inputStream))
                while (this.isActive) {
                    val line = reader.readLine()
                    if (!line.isNullOrBlank()) {
                        listener.invoke(line.contains("dark", true))
                    }
                }
                monitorProcess.destroy()
            } catch (ignored: Throwable) { }
        }

        GlobalScope.launchIO {
            val legacyProcess = runtime.exec(Constants.LINUX_THEME_MONITOR_LEGACY_CMD)
            try {
                val reader = BufferedReader(InputStreamReader(legacyProcess.inputStream))
                while (this.isActive) {
                    val line = reader.readLine()
                    if (!line.isNullOrBlank()) {
                        listener.invoke(line.contains("dark", true))
                    }
                }
                legacyProcess.destroy()
            } catch (ignored: Throwable) { }
        }
    }

    override fun listen(listener: (isDark: Boolean) -> Unit) {
        this.listener = listener
    }
}