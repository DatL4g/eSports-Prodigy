package dev.datlag.esports.prodigy.ui.screen.home.info.device

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberAsyncImagePainter
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.color.createTheme
import dev.datlag.esports.prodigy.color.theme.Theme
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.withIOContext
import dev.datlag.esports.prodigy.common.withMainContext
import dev.datlag.esports.prodigy.game.common.containsInvalidEntries
import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.datlag.esports.prodigy.ui.screen.home.asyncImageLoader
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameComponent
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameConfig
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameView
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay

@Composable
actual fun DeviceView(component: DeviceComponent) {
    when (LocalWindowSize.current) {
        is WindowSize.EXPANDED -> ExpandedView(component)
        else -> DefaultView(component)
    }
}

@Composable
fun DefaultView(component: DeviceComponent) {
    val childState by component.child.subscribeAsState()
    childState.overlay?.also { (config, instance) ->
        when (config) {
            is GameConfig.Overview -> {
                GameView(instance as GameComponent)
            }
            else -> {
                MainView(component, Modifier.fillMaxWidth())
            }
        }
    } ?: MainView(component, Modifier.fillMaxWidth())
}

@Composable
fun ExpandedView(component: DeviceComponent) {
    val childState by component.child.subscribeAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val modifier = when (childState.overlay?.configuration) {
            is GameConfig.Overview -> Modifier.widthIn(max = 700.dp)
            else -> Modifier.widthIn(max = 700.dp)
        }
        MainView(component, modifier)

        childState.overlay?.also { (config, instance) ->
            when (config) {
                is GameConfig.Overview -> {
                    Box(
                        modifier = Modifier.weight(2F)
                    ) {
                        GameView(instance as GameComponent)
                    }
                }
                else -> { }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainView(component: DeviceComponent, modifier: Modifier = Modifier) {
    val gameManifests by component.gameManifests.collectAsStateSafe { emptyList() }

    LazyColumn(modifier = modifier.padding(horizontal = 16.dp)) {
        item {
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = {

                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(SharedRes.strings.settings)
                    )
                }
                Spacer(modifier = Modifier.weight(1F))
                FilledIconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = {

                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(SharedRes.strings.user)
                    )
                }
            }
        }

        item {
            Text(
                text = "Your installed",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Text(
                text = "eSport Games",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(gameManifests) { game ->
            Column(
                modifier = Modifier.onClick {
                    component.gameClicked(game)
                }
            ) {
                val placeholder = if (game is Game.Steam && game.headerFile != null) {
                    rememberAsyncImagePainter(
                        request = ImageRequest(game.headerFile!!),
                        contentScale = ContentScale.FillWidth
                    )
                } else {
                    null
                }
                val image = asyncImageLoader(
                    request = ImageRequest(game.headerUrl ?: (game as? Game.Steam)?.headerFile ?: String()),
                    contentScale = ContentScale.FillWidth,
                    onLoading = {
                        placeholder
                    },
                    onFailure = {
                        placeholder
                    }
                )

                val density = LocalDensity.current

                LaunchedEffect(image, game) {
                    withIOContext {
                        while (SchemeTheme.themes[game] == null) {
                            if (image.intrinsicSize != Size.Unspecified && image.intrinsicSize != Size.Zero) {
                                val theme = image.toAwtImage(
                                    density,
                                    LayoutDirection.Ltr
                                ).createTheme()

                                SchemeTheme.themes[game] = theme
                            }
                            delay(100)
                        }
                    }
                }

                Card(
                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
                ) {
                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        painter = image,
                        contentDescription = game.name,
                        contentScale = ContentScale.FillWidth
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(1F),
                        text = game.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (game.dxvkCaches.containsInvalidEntries()) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = stringResource(SharedRes.strings.dxvk_contains_invalid_entries),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}