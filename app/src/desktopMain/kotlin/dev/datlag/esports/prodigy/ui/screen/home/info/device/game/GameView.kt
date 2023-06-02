package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import dev.datlag.esports.prodigy.model.common.homeDirectory
import dev.datlag.esports.prodigy.model.common.scopeCatching
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.datlag.esports.prodigy.ui.screen.home.asyncImageLoader
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.runBlocking
import java.awt.Desktop
import java.io.File

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
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val startError = stringResource(SharedRes.strings.could_not_start_game)
                    var launchClickable by remember { mutableStateOf(true) }

                    Button(
                        onClick = {
                            if (launchClickable) {
                                if (component.game is Game.Steam) {
                                    launchClickable = false

                                    val appId = (component.game as Game.Steam).manifest.appId
                                    var result = "steam://rungameid/$appId".openInBrowser(startError)
                                    if (result.isFailure) {
                                        result = "steam://run/$appId".openInBrowser(startError)
                                    }
                                    launchClickable = result.isFailure
                                }
                            }
                        },
                        enabled = launchClickable
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

                    var openSupported by remember { mutableStateOf(
                        scopeCatching {
                            Desktop.isDesktopSupported()
                                    && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)
                                    && component.game.directory != null
                        }.getOrNull() ?: false
                    ) }

                    OutlinedButton(
                        onClick = {
                            val result = scopeCatching {
                                Desktop.getDesktop().open(component.game.directory)
                            }
                            openSupported = result.isSuccess
                        },
                        enabled = openSupported,
                        border = BorderStroke(
                            width = ButtonDefaults.outlinedButtonBorder.width,
                            color = if (openSupported) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12F)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null
                        )
                        Text(
                            text = "Location",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
