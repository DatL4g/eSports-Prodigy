package dev.datlag.esports.prodigy.ui.theme

import com.sun.jna.platform.win32.Advapi32
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.W32Errors
import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinReg
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.other.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

object WindowsThemeDetector : ThemeDetector {

    override val isDark: Boolean
        get() {
            return Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, Constants.WIN_THEME_REGISTRY_PATH, Constants.WIN_THEME_REGISTRY_VALUE)
                    && Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, Constants.WIN_THEME_REGISTRY_PATH, Constants.WIN_THEME_REGISTRY_VALUE) == 0
        }

    private var lastDetection = isDark

    private var listener: (isDark: Boolean) -> Unit = { }

    init {
        GlobalScope.launchIO {
            val hkey = WinReg.HKEYByReference()
            var err = Advapi32.INSTANCE.RegOpenKeyEx(WinReg.HKEY_CURRENT_USER, Constants.WIN_THEME_REGISTRY_PATH, 0, WinNT.KEY_READ, hkey)
            if (err != W32Errors.ERROR_SUCCESS) {
                Advapi32Util.registryCloseKey(hkey.value)
                this.cancel(err.toString())
            }

            while (this.isActive) {
                err = Advapi32.INSTANCE.RegNotifyChangeKeyValue(hkey.value, false, WinNT.REG_NOTIFY_CHANGE_LAST_SET, null, false)
                if (err != W32Errors.ERROR_SUCCESS) {
                    Advapi32Util.registryCloseKey(hkey.value)
                    this.cancel(err.toString())
                }

                val newDark = isDark
                if (lastDetection != newDark) {
                    lastDetection = newDark
                    listener.invoke(newDark)
                }
            }
            Advapi32Util.registryCloseKey(hkey.value)
        }
    }

    override fun listen(listener: (isDark: Boolean) -> Unit) {
        this.listener = listener
    }
}