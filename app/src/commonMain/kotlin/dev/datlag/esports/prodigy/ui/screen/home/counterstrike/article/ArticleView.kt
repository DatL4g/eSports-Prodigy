package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.article

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle
import web.WebView
import web.rememberWebViewState
import web.rememberWebViewStateWithHTMLData

@Composable
fun ArticleView(component: ArticleComponent) {
    val state = rememberWebViewState(component.href)

    WebView(state, modifier = Modifier.fillMaxSize())
}