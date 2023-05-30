package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberAsyncImagePainter
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.common.openInBrowser
import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.datlag.esports.prodigy.ui.screen.home.asyncImageLoader
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun GameView(component: GameComponent) {
    SchemeTheme.themes[component.game]?.let {
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
                val placeholder = if (game is Game.Steam && game.heroFile != null) {
                    rememberAsyncImagePainter(
                        request = ImageRequest(game.heroFile!!),
                        contentScale = ContentScale.FillWidth
                    )
                } else {
                    null
                }
                val image = asyncImageLoader(
                    request = ImageRequest(game.heroUrl ?: (game as? Game.Steam)?.heroFile ?: String()),
                    contentScale = ContentScale.FillWidth,
                    onLoading = {
                        placeholder
                    },
                    onFailure = {
                        placeholder
                    }
                )

                val modifier = when (LocalWindowSize.current) {
                    WindowSize.COMPACT -> Modifier.fillMaxWidth()
                    WindowSize.MEDIUM -> Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    WindowSize.EXPANDED -> Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                }
                Box(
                    modifier = modifier
                ) {
                    Image(
                        painter = image,
                        contentDescription = game.name,
                        contentScale = ContentScale.FillWidth
                    )
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
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                    text = component.game.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    val startError = stringResource(SharedRes.strings.could_not_start_game)
                    var clickable by remember { mutableStateOf(true) }

                    Button(
                        onClick = {
                            if (clickable) {
                                if (component.game is Game.Steam) {
                                    clickable = false

                                    val appId = (component.game as Game.Steam).manifest.appId
                                    var result = "steam://rungameid/$appId".openInBrowser(startError)
                                    if (result.isFailure) {
                                        result = "steam://run/$appId".openInBrowser(startError)
                                    }
                                    clickable = result.isFailure
                                }
                            }
                        },
                        enabled = clickable
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(SharedRes.strings.play)
                        )
                        Text(
                            text = "Launch",
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
