package dev.datlag.esports.prodigy.ui.screen.home.info.device

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.game.common.containsInvalidEntries
import dev.datlag.esports.prodigy.game.model.LocalGame
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameComponent
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameConfig
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameView
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.kamel.core.Resource
import io.kamel.image.lazyPainterResource

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
    val games by component.games.collectAsStateSafe { emptyList() }

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
                        component.navigateToUser()
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

        items(games) { game ->
            Column(
                modifier = Modifier.onClick {
                    component.gameClicked(game)
                }
            ) {
                Card(
                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
                ) {
                    GameHeader(component, game)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val caches by game.dxvkCaches.collectAsStateSafe { emptyMap() }

                    Text(
                        modifier = Modifier.weight(1F),
                        text = game.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (caches.values.flatten().containsInvalidEntries()) {
                        TooltipArea(
                            tooltip = {
                                Text(
                                    modifier = Modifier.clip(
                                        RoundedCornerShape(4.dp)
                                    ).background(MaterialTheme.colorScheme.errorContainer).padding(4.dp),
                                    text = "Invalid DXVK entries",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        ) {
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

        item {
            Text(
                modifier = Modifier.height(400.dp),
                text = "More content"
            )
        }
    }
}

@Composable
fun ColumnScope.GameHeader(component: DeviceComponent, game: LocalGame) {
    when (val resource = lazyPainterResource(game.headerUrl)) {
        is Resource.Loading -> {
            LoadingImage(game.name, resource.progress)
        }
        is Resource.Success -> {
            val painter = resource.value
            loadGameScheme(component, game.name, painter)
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painter,
                contentDescription = game.name,
                contentScale = ContentScale.FillWidth
            )
        }
        is Resource.Failure -> {
            val fallbackFile = game.headerFile
            if (fallbackFile != null) {
                when (val fallbackResource = lazyPainterResource(fallbackFile)) {
                    is Resource.Loading -> LoadingImage(game.name, fallbackResource.progress)
                    is Resource.Success -> {
                        val painter = fallbackResource.value
                        loadGameScheme(component, game.name, painter)
                        Image(
                            modifier = Modifier.fillMaxWidth(),
                            painter = painter,
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

@Composable
fun loadGameScheme(component: DeviceComponent, title: String, painter: Painter) {
    val awtImage = painter.toAwtImage(
        LocalDensity.current,
        LayoutDirection.Ltr
    )

    component.loadSchemeFor(title, awtImage)
}

@Composable
fun FallbackImage(gameTitle: String) {
    Image(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 200.dp),
        imageVector = Icons.Default.NoPhotography,
        contentDescription = gameTitle,
        contentScale = ContentScale.Inside,
        alignment = Alignment.Center,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
    )
}

@Composable
fun LoadingImage(gameTitle: String, percentage: Float) {
    val painter = when {
        percentage >= 0.9F -> painterResource(SharedRes.images.clock_loader_90)
        percentage >= 0.8F -> painterResource(SharedRes.images.clock_loader_80)
        percentage >= 0.6F -> painterResource(SharedRes.images.clock_loader_60)
        percentage >= 0.4F -> painterResource(SharedRes.images.clock_loader_40)
        percentage >= 0.2F -> painterResource(SharedRes.images.clock_loader_20)
        else -> painterResource(SharedRes.images.clock_loader_10)
    }
    Image(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 200.dp),
        painter = painter,
        contentDescription = gameTitle,
        contentScale = ContentScale.Inside,
        alignment = Alignment.Center,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
    )
}