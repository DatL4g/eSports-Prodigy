package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.article

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import dev.datlag.esports.prodigy.ui.LocalCommonizer
import dev.datlag.esports.prodigy.ui.LocalRestartRequired

@Composable
fun ArticleView(component: ArticleComponent) {
    if (LocalRestartRequired.current) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val commonizer = LocalCommonizer.current

            Text(text = "Application restart required.")
            Button(
                onClick = {
                    commonizer.restartApp()
                }
            ) {
                Text(text = "Restart")
            }
        }
    } else {
        val state = rememberWebViewState(component.href)

        WebView(state, modifier = Modifier.fillMaxSize())
    }
}