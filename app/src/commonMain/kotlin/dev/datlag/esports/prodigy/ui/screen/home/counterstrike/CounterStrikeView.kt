@file:Suppress("INLINE_FROM_HIGHER_PLATFORM")

package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.datlag.esports.prodigy.common.*
import dev.datlag.esports.prodigy.model.common.safeSubList
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.network.Status
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.datlag.esports.prodigy.ui.screen.home.counterstrike.components.NewsCard
import dev.datlag.esports.prodigy.ui.screen.home.counterstrike.components.TeamCard
import dev.datlag.esports.prodigy.ui.screen.home.counterstrike.team.TeamComponent
import dev.datlag.esports.prodigy.ui.screen.home.counterstrike.team.TeamViewComponent
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.http.*

@Composable
fun CounterStrikeView(component: CounterStrikeComponent) {
    when (LocalWindowSize.current) {
        is WindowSize.EXPANDED -> ExpandedView(component)
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
    val home by component.home.collectAsStateSafe { null }
    val homeStatus by component.homeStatus.collectAsStateSafe { Status.LOADING }

    if (homeStatus is Status.LOADING) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(400.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (home?.hero != null) {
                fullRow {
                    HeroImage(home!!.hero!!) {

                    }
                }
            }
            if (!home?.teams.isNullOrEmpty()) {
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
                fullRowItems(home?.teams ?: emptyList()) { team ->
                    TeamCard(team) {
                        component.teamClicked(team)
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