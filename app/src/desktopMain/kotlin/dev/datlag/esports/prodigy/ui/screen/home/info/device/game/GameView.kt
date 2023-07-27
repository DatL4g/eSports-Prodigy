package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.common.collectAsStateSafe
import dev.datlag.esports.prodigy.common.openInBrowser
import dev.datlag.esports.prodigy.game.SteamLauncher
import dev.datlag.esports.prodigy.game.model.LocalGameInfo
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components.*
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun GameView(component: GameComponent) {
    val caches by component.game.dxvkCaches.collectAsStateSafe { emptyMap() }
    val unsupportedUserChartGame = component.unsupportedUserChartGames.firstOrNull {
        it.steamId != null && it.steamId.equals(component.game.steam?.manifest?.appId, true)
                || component.game.name.contains(it.name, true)
                || it.name.contains(component.game.name, true)
    }

    val (columnPadding, extraPadding) = when (calculateWindowSizeClass().widthSizeClass) {
        WindowWidthSizeClass.Medium -> PaddingValues(16.dp) to PaddingValues(0.dp)
        WindowWidthSizeClass.Expanded -> PaddingValues(16.dp) to PaddingValues(0.dp)
        else -> PaddingValues(0.dp) to PaddingValues(horizontal = 16.dp)
    }
    SchemeTheme(component.game.name) {
        LazyColumn(contentPadding = columnPadding) {
            item {
                val game = component.game

                val modifier = when (calculateWindowSizeClass().widthSizeClass) {
                    WindowWidthSizeClass.Medium -> Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    WindowWidthSizeClass.Expanded -> Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    else -> Modifier.fillMaxWidth()
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

            if (caches.flatMap { c -> c.value }.isNotEmpty()) {
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
                    val users by SteamLauncher.loggedInUsers.collectAsStateSafe { emptyList() }
                    var width by remember(component.game.name) { mutableStateOf(0) }
                    var height by remember(component.game.name) { mutableStateOf(0) }

                    FlowRow(
                        modifier = Modifier.padding(extraPadding),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        users.forEach { user ->
                            val avatar = remember(user.id) { component.loadUserAvatar(user) }

                            UserChartCard(
                                user = user,
                                width = width,
                                height = height,
                                avatar = avatar,
                                onSizeChange = {
                                    width = it.first
                                    height = it.second
                                }
                            ) {
                                // ToDo("load stats")
                            }
                        }
                    }
                }
            }
        }
    }
}


