@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.datlag.esports.prodigy.common.*
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.state.cs.HomeRequest
import dev.datlag.esports.prodigy.ui.screen.home.counterstrike.components.TeamCard
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun CounterStrikeView(component: CounterStrikeComponent) {
    when (calculateWindowSizeClass().widthSizeClass) {
        WindowWidthSizeClass.Expanded -> ExpandedView(component)
        else -> DefaultView(component)
    }
}

@Composable
private fun DefaultView(component: CounterStrikeComponent) {
    val childState by component.child.subscribeAsState()
    childState.child?.also { (_, instance) ->
        instance.render()
    } ?: MainView(component, Modifier.fillMaxWidth())
}

@Composable
private fun ExpandedView(component: CounterStrikeComponent) {
    val childState by component.child.subscribeAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MainView(component, Modifier.widthIn(max = 700.scaledDp(200.dp)))

        childState.child?.also { (_, instance) ->
            Box(
                modifier = Modifier.weight(2F)
            ) {
                instance.render()
            }
        }
    }
}

@Composable
private fun MainView(component: CounterStrikeComponent, modifier: Modifier) {
    val homeState by component.homeRequestState.collectAsStateWithLifecycle(HomeRequest.Loading(null))

    when (val currentState = homeState) {
        is HomeRequest.Loading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        is HomeRequest.Error -> {
            // ToDo("Display error, retry button")
        }
        is HomeRequest.Success -> {
            LazyVerticalGrid(
                modifier = modifier,
                columns = GridCells.Adaptive(400.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentState.home.hero != null) {
                    fullRow {
                        HeroImage(currentState.home.hero!!) {
                            component.articleClicked(currentState.home.hero!!.href)
                        }
                    }
                }
                if (currentState.home.teams.isNotEmpty()) {
                    fullRow {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Teams",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = {

                                }
                            ) {
                                Text(text = "World Ranking")
                            }
                        }
                    }
                    fullRowItemsIndexed(currentState.home.teams) { index, team ->
                        TeamCard(team, index) {
                            component.teamClicked(team)
                        }
                    }
                }
                if (currentState.home.news.isNotEmpty()) {
                    fullRow {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "News",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = {
                                    component.newsClicked()
                                }
                            ) {
                                Text(text = "More articles")
                            }
                        }
                    }
                    fullRowItems(currentState.home.news) { news ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = news.title,
                                modifier = Modifier.weight(1F)
                            )
                            Button(
                                onClick = {
                                    component.articleClicked(news.href)
                                }
                            ) {
                                Text(text = "Details")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroImage(hero: Home.Hero, onClick: () -> Unit) {
    Card(
        modifier = Modifier.onClick {
            onClick()
        }
    ) {
        when (val resource = asyncPainterResource(hero.img)) {
            is Resource.Loading -> {

            }
            is Resource.Success -> {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = resource.value,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth
                )
            }
            is Resource.Failure -> {

            }
        }
    }
}