package dev.datlag.esports.prodigy.ui.custom

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.datlag.esports.prodigy.ui.browser.CefBrowserAwt

@Composable
actual fun PlatformBrowser(url: String) {
    CefBrowserAwt(
        url = url,
        errorContent = {
            Column {
                Text(text = "Error Loading $url")
                Text(text = it.message.toString())
            }
        },
        initContent = {
            Text(text = "Loading: ${it.progress}")
        }
    )
}