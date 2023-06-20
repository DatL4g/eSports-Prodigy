package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.fullRow
import dev.datlag.esports.prodigy.model.common.safeSubList
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.datlag.esports.prodigy.ui.screen.home.counterstrike.components.NewsCard

@Composable
fun CounterStrikeView(component: CounterStrikeComponent) {
    when (LocalWindowSize.current) {
        is WindowSize.EXPANDED -> ExpandedView(component)
        else -> DefaultView(component)
    }
}

@Composable
private fun DefaultView(component: CounterStrikeComponent) {
    MainView(component, Modifier.fillMaxWidth())
}

@Composable
private fun ExpandedView(component: CounterStrikeComponent) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MainView(component, Modifier.widthIn(max = 700.dp))
        Box(modifier = Modifier.weight(2F)) {
            Text(text = "Expanded")
        }
    }
}

@Composable
private fun MainView(component: CounterStrikeComponent, modifier: Modifier) {
    val news by component.news.collectAsStateSafe { emptyList() }
    var newsPagination by remember { mutableStateOf(5) }
    var newsWidth by remember(news.size) { mutableStateOf(0) }
    var newsHeight by remember(news.size) { mutableStateOf(0) }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(400.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        fullRow {
            Text(
                text = "News",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(news.safeSubList(0, newsPagination)) {
            NewsCard(it, newsWidth, newsHeight) { (newWidth, newHeight) ->
                newsWidth = newWidth
                newsHeight = newHeight
            }
        }
        fullRow {
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                Button(
                    onClick = {
                        newsPagination += 5
                    }
                ) {
                    Text(text = "Show more")
                }
            }
        }
    }
}