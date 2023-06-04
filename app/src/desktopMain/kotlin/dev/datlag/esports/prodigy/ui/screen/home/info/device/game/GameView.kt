package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components.DirectoryButton
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components.GameHero
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components.GameLauncherIcons
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components.LaunchButton
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GameView(component: GameComponent) {
    SchemeTheme.themes[component.game.name]?.let {
        SchemeTheme.specificColorScheme(it)
    }

    SchemeTheme {
        LazyColumn(modifier = when (LocalWindowSize.current) {
            is WindowSize.COMPACT -> Modifier
            is WindowSize.MEDIUM -> Modifier.padding(16.dp)
            is WindowSize.EXPANDED -> Modifier.padding(16.dp)
        }) {
            item {
                val game = component.game

                val modifier = when (LocalWindowSize.current) {
                    WindowSize.COMPACT -> Modifier.fillMaxWidth()
                    WindowSize.MEDIUM -> Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    WindowSize.EXPANDED -> Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                }
                Box(
                    modifier = modifier
                ) {
                    GameHero(game)
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                component.goBack()
                            },
                            modifier = Modifier.background(
                                color = Color.Black.copy(alpha = 0.5F),
                                shape = CircleShape
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1F),
                        text = component.game.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    GameLauncherIcons(component.game)
                }
            }
            item {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LaunchButton(component.game)
                    DirectoryButton(component.game)
                }
            }

            if (component.game.dxvkCaches.isNotEmpty()) {
                item {
                    var expand by remember { mutableStateOf(false) }
                    OutlinedCard(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp).fillMaxWidth(),
                        onClick = {
                            expand = !expand
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DXVK Cache",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.weight(1F))
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Expand"
                            )
                        }
                        if (expand) {
                            Divider()
                            Text(text = "Expanded")
                        }
                    }
                }
            }

        }
    }
}


