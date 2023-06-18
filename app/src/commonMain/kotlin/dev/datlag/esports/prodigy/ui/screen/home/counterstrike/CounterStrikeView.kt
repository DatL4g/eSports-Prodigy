package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.model.common.safeSubList

@Composable
fun CounterStrikeView(component: CounterStrikeComponent) {
    val news by component.news.collectAsStateSafe { emptyList() }
    var newsPagination by remember { mutableStateOf(5) }

    LazyColumn {
        item {
            Text(
                text = "News",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(news.safeSubList(0, newsPagination)) {
            Card(modifier = Modifier.fillParentMaxWidth()) {
                Row(modifier = Modifier.fillParentMaxWidth().padding(16.dp)) {
                    Text(text = it.title)
                    Spacer(modifier = Modifier.weight(1F))
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open in Browser"
                        )
                    }
                }
            }
        }

        if (newsPagination < news.size) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
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
}