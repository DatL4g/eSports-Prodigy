package dev.datlag.esports.prodigy.ui.theme

import org.apache.commons.lang3.SystemUtils

interface ThemeDetector {

    abstract val isDark: Boolean

    abstract fun listen(listener: (isDark: Boolean) -> Unit)

    companion object {
        fun create(): ThemeDetector {
            return when {
                SystemUtils.IS_OS_LINUX -> GnomeThemeDetector
                SystemUtils.IS_OS_MAC -> MacThemeDetector
                SystemUtils.IS_OS_WINDOWS -> WindowsThemeDetector
                else -> object : ThemeDetector {
                    override val isDark: Boolean
                        get() = false

                    override fun listen(listener: (isDark: Boolean) -> Unit) { }
                }
            }
        }
    }
}
