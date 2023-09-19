package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.article

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle

@Composable
fun ArticleView(component: ArticleComponent) {
    WebView(rememberWebViewState(component.href), Modifier.fillMaxSize())
}