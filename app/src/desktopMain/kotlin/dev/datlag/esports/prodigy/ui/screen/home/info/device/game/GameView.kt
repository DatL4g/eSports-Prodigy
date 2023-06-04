package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.datlag.esports.prodigy.ui.screen.home.info.device.FallbackImage
import dev.datlag.esports.prodigy.ui.screen.home.info.device.LoadingImage
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.buttons.DirectoryButton
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.buttons.LaunchButton
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.core.Resource
import io.kamel.image.lazyPainterResource

@OptIn(ExperimentalLayoutApi::class)
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
                    if (component.game is Game.Steam) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(SharedRes.images.steam),
                            contentDescription = "Steam"
                        )
                    }
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
        }
    }
}

@Composable
fun BoxScope.GameHero(game: Game) {
    when (val resource = lazyPainterResource(game.heroUrl ?: run {
        when (game) {
            is Game.Steam -> game.heroFile
            is Game.Multi -> game.heroFile
            else -> null
        }
    } ?: String())) {
        is Resource.Loading -> {
            LoadingImage(game.name, resource.progress)
        }
        is Resource.Success -> {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = resource.value,
                contentDescription = game.name,
                contentScale = ContentScale.FillWidth
            )
        }
        is Resource.Failure -> {
            val fallbackFile = when (game) {
                is Game.Steam -> game.heroFile
                is Game.Multi -> game.heroFile
                else -> null
            }

            if (fallbackFile != null) {
                when (val fallbackResource = lazyPainterResource(fallbackFile)) {
                    is Resource.Loading -> {
                        LoadingImage(game.name, fallbackResource.progress)
                    }
                    is Resource.Success -> {
                        Image(
                            modifier = Modifier.fillMaxWidth(),
                            painter = fallbackResource.value,
                            contentDescription = game.name,
                            contentScale = ContentScale.FillWidth
                        )
                    }
                    is Resource.Failure -> {
                        FallbackImage(game.name)
                    }
                }
            } else {
                FallbackImage(game.name)
            }
        }
    }
}
