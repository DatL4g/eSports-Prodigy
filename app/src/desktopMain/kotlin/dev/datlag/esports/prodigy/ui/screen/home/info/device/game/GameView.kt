package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

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
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.netguru.multiplatform.charts.ChartAnimation
import com.netguru.multiplatform.charts.line.LineChart
import com.netguru.multiplatform.charts.line.LineChartData
import com.netguru.multiplatform.charts.line.LineChartPoint
import com.netguru.multiplatform.charts.line.LineChartSeries
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.openInBrowser
import dev.datlag.esports.prodigy.game.model.LocalGameInfo
import dev.datlag.esports.prodigy.model.common.asList
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components.*
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme
import kotlinx.serialization.json.JsonNull.content

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameView(component: GameComponent) {
    val caches by component.game.dxvkCaches.collectAsStateSafe { emptyMap() }
    val unsupportedUserChartGame = component.unsupportedUserChartGames.firstOrNull {
        it.steamId != null && it.steamId.equals(component.game.steam?.manifest?.appId, true)
                || component.game.name.contains(it.name, true)
                || it.name.contains(component.game.name, true)
    }

    SchemeTheme.themes[component.game.name]?.let {
        SchemeTheme.specificColorScheme(it)
    }

    val (columnPadding, extraPadding) = when (LocalWindowSize.current) {
        is WindowSize.COMPACT -> PaddingValues(0.dp) to PaddingValues(horizontal = 16.dp)
        is WindowSize.MEDIUM -> PaddingValues(16.dp) to PaddingValues(0.dp)
        is WindowSize.EXPANDED -> PaddingValues(16.dp) to PaddingValues(0.dp)
    }
    SchemeTheme {
        LazyColumn(contentPadding = columnPadding) {
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
                    HeroBackButton {
                        component.goBack()
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(extraPadding).padding(vertical = 16.dp),
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(extraPadding)
                ) {
                    LaunchButton(component.game)
                    DirectoryButton(component.game)
                }
            }

            if (caches.isNotEmpty()) {
                item {
                    Text(
                        modifier = Modifier.padding(extraPadding).padding(top = 32.dp, bottom = 16.dp),
                        text = "DXVK State Cache",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                item {
                    FlowRow(
                        modifier = Modifier.padding(extraPadding),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        var width by remember(component.game.name) { mutableStateOf(0) }
                        var height by remember(component.game.name) { mutableStateOf(0) }

                        caches.forEach { (type, cacheList) ->
                            cacheList.forEach { cache ->
                                val gameInfo = when (type) {
                                    is LocalGameInfo.TYPE.STEAM -> component.game.steam
                                    is LocalGameInfo.TYPE.HEROIC -> component.game.heroic
                                }
                                CacheCard(component.game, gameInfo, type, cache, width, height) {
                                    width = it.first
                                    height = it.second
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    modifier = Modifier.padding(extraPadding).padding(top = 32.dp, bottom = 16.dp),
                    text = "User statistics",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            if (unsupportedUserChartGame != null) {
                item {
                    UnsupportedGameChart(
                        modifier = Modifier.padding(extraPadding)
                    ) {
                        unsupportedUserChartGame.learnMoreUrl.openInBrowser("Cannot open URL")
                    }
                }
            } else {
                item {
                    Text(
                        modifier = Modifier.padding(extraPadding),
                        text = "Coming soon..."
                    )
                }
            }
        }
    }
}


