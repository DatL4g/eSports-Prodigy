package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.article

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle

@Composable
fun ArticleView(component: ArticleComponent) {
    val content by component.content.collectAsStateWithLifecycle(initialValue = null)

    if (content.isNullOrBlank()) {
        Text(text = "Loading")
    } else {
        Text(text = "Finished")
    }
}