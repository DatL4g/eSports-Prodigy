package dev.datlag.esports.prodigy.ui.theme

import com.sun.jna.Callback
import de.jangassen.jfa.foundation.Foundation
import de.jangassen.jfa.foundation.ID

object MacThemeDetector : ThemeDetector {

    override val isDark: Boolean
        get() {
            val pool = Foundation.NSAutoreleasePool()
            try {
                val userDefaults = Foundation.invoke("NSUserDefaults", "standardUserDefaults")
                val appleInterfaceStyle = Foundation.toStringViaUTF8(
                    Foundation.invoke(
                        userDefaults,
                        "objectForKey:",
                        Foundation.nsString("AppleInterfaceStyle")
                    )
                )
                return (appleInterfaceStyle ?: String()).contains("dark", true)
            } finally {
                pool.drain()
            }
        }

    private var listener: (isDark: Boolean) -> Unit = { }

    private val themeChangedCallback: Callback = object : Callback {
        fun callback() {
            listener.invoke(isDark)
        }
    }

    init {
        val pool = Foundation.NSAutoreleasePool()
        try {
            val delegateClass = Foundation.allocateObjcClassPair(
                Foundation.getObjcClass("NSObject"),
                "NSColorChangesObserver"
            )
            if (!ID.NIL.equals(delegateClass)) {
                Foundation.addMethod(
                    delegateClass,
                    Foundation.createSelector("handleAppleThemeChanged:"),
                    themeChangedCallback,
                    "v@"
                )
                Foundation.registerObjcClassPair(delegateClass)
            }

            val delegate = Foundation.invoke("NSColorChangesObserver", "new")
            Foundation.invoke(
                Foundation.invoke("NSDistributedNotificationCenter", "defaultCenter"),
                "addObserver:selector:name:object:",
                delegate,
                Foundation.createSelector("handleAppleThemeChanged:"),
                Foundation.nsString("AppleInterfaceThemeChangedNotification"),
                ID.NIL
            )
        } finally {
            pool.drain()
        }
    }

    override fun listen(listener: (isDark: Boolean) -> Unit) {
        this.listener = listener
    }
}