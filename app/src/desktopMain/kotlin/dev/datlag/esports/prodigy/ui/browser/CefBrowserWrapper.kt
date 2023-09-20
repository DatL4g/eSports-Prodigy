package dev.datlag.esports.prodigy.ui.browser

import androidx.compose.ui.awt.ComposeWindow
import org.cef.CefClient
import org.cef.browser.CefBrowser
import org.cef.browser.CefRequestContext

sealed interface CefBrowserWrapper {
    val browser: CefBrowser
}

class CefBrowserAwtWrapper(
    client: CefClient,
    url: String,
    osr: Boolean,
    transparent: Boolean,
    context: CefRequestContext? = null
) : CefBrowserWrapper {
    override val browser: CefBrowser = client.createBrowser(url, osr, transparent, context)
}