package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.article

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.markdownColor
import com.mikepenz.markdown.model.markdownTypography
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle

@Composable
fun ArticleView(component: ArticleComponent) {
    val content by component.content.collectAsStateWithLifecycle(initialValue = null)

    if (content.isNullOrBlank()) {
        Text(text = "Loading")
    } else {
        Markdown(
            content = content!!
        )
    }
}