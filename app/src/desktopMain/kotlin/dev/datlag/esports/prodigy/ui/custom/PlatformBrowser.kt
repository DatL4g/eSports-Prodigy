package dev.datlag.esports.prodigy.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.ui.browser.ApplicationDisposer
import dev.datlag.esports.prodigy.ui.browser.ApplicationRestartRequiredException
import dev.datlag.esports.prodigy.ui.browser.CefBrowserAwt

@Composable
actual fun PlatformBrowser(url: String) {
    CefBrowserAwt(
        url = url,
        errorContent = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (it is ApplicationRestartRequiredException) {
                    val disposer = ApplicationDisposer.current

                    Text(text = "Application restart required.")
                    Button(
                        onClick = {
                            disposer.restart()
                        }
                    ) {
                        Text(text = "Restart")
                    }
                } else {
                    Text(text = "Error Loading $url")
                    Text(text = it.message.toString())
                }
            }
        },
        initContent = {
            Text(text = "Loading: ${it.progress}")
        }
    )
}