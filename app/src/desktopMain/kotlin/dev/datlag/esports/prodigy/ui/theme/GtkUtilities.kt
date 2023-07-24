package dev.datlag.esports.prodigy.ui.theme

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import dev.datlag.esports.prodigy.model.common.scopeCatching

object GtkUtilities {

    fun scaleFactor(): Double {
        val gtkHandle = gtkInstance()
        return scopeCatching {
            if (gtkHandle != null && gtkHandle.gtk_init_check(0, null)) {
                when (gtkHandle) {
                    is GTK2 -> gtk2ScaleFactor(gtkHandle)
                    is GTK3 -> gtk3ScaleFactor(gtkHandle)
                    else -> {
                        0.0
                    }
                }
            } else {
                0.0
            }
        }.getOrNull() ?: 0.0
    }

    private fun gtkInstance(): GTK? {
        return when (gtkMajorVersion()) {
            2 -> GTK2.INSTANCE
            3 -> GTK3.INSTANCE
            else -> scopeCatching {
                GTK3.INSTANCE
            }.getOrNull() ?: scopeCatching {
                GTK2.INSTANCE
            }.getOrNull()
        }
    }

    private fun gtkMajorVersion(): Int {
        return scopeCatching {
            val toolkitClass = Class.forName("sun.awt.UNIXToolkit")
            val versionMethod = toolkitClass.getDeclaredMethod("getGtkVersion")
            val versionInfo = versionMethod.invoke(toolkitClass) as Enum<*>
            val numberMethod = versionInfo::class.java.getDeclaredMethod("getNumber")
            numberMethod.invoke(versionInfo) as? Int
        }.getOrNull() ?: 0
    }

    private fun gtk2ScaleFactor(gtk2: GTK2): Double {
        val display = gtk2.gdk_display_get_default()
        val screen = gtk2.gdk_display_get_default_screen(display)
        return gtk2.gdk_screen_get_resolution(screen) / 96.0
    }

    private fun gtk3ScaleFactor(gtk3: GTK3): Double {
        val display = gtk3.gdk_display_get_default()
        val minorVersion = gtk3.gtk_get_minor_version()
        return when {
            minorVersion < 10 -> 0.0
            minorVersion >= 22 -> {
                val monitor = gtk3.gdk_display_get_primary_monitor(display)
                gtk3.gdk_monitor_get_scale_factor(monitor).toDouble()
            }
            minorVersion >= 10 -> {
                val screen = gtk3.gdk_display_get_default_screen(display)
                gtk3.gdk_screen_get_monitor_scale_factor(screen, 0).toDouble()
            }
            else -> 0.0
        }
    }

    private interface GTK : Library {
        fun gtk_init_check(argc: Int, argv: Array<String>?): Boolean
        fun gdk_display_get_default(): Pointer
        fun gdk_display_get_default_screen(display: Pointer): Pointer
    }

    private interface GTK3 : GTK {
        fun gtk_get_minor_version(): Int
        fun gdk_screen_get_monitor_scale_factor(screen: Pointer, monitor_num: Int): Int
        fun gdk_display_get_primary_monitor(display: Pointer): Pointer
        fun gdk_monitor_get_scale_factor(monitor: Pointer): Int

        companion object {
            val INSTANCE = Native.load("gtk-3", GTK3::class.java)
        }
    }

    private interface GTK2 : GTK {
        fun gdk_screen_get_resolution(screen: Pointer): Double

        companion object {
            val INSTANCE = Native.loadLibrary("gtk-x11-2.0", GTK2::class.java)
        }
    }

}