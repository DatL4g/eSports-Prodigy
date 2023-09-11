package dev.datlag.esports.prodigy.ui.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pending
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.*
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.observe
import dev.datlag.esports.prodigy.ui.navigation.Component
import dev.datlag.esports.prodigy.ui.screen.home.counterstrike.CounterStrikeViewComponent
import dev.datlag.esports.prodigy.ui.screen.home.info.InfoViewComponent
import org.kodein.di.DI
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.ui.screen.home.other.OtherScreenComponent
import dev.datlag.esports.prodigy.ui.screen.home.rocketleague.RocketLeagueViewComponent

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val goToUser: () -> Unit,
    private val goToSettings: (offset: Offset?) -> Unit
) : HomeComponent, ComponentContext by componentContext {

    override val pagerItems = listOf(
        HomeComponent.PagerItem(
            0,
            SharedRes.strings.home,
            Icons.Default.Home
        ),
        HomeComponent.PagerItem(
            1,
            SharedRes.strings.counter_strike,
            SharedRes.images.counter_strike
        ),
        HomeComponent.PagerItem(
            2,
            SharedRes.strings.rocket_league,
            SharedRes.images.rocket_league
        ),
        HomeComponent.PagerItem(
            3,
            SharedRes.strings.other,
            Icons.Default.Pending
        )
    )

    @OptIn(ExperimentalDecomposeApi::class)
    private val pagesNavigation = PagesNavigation<View>()

    @OptIn(ExperimentalDecomposeApi::class)
    override val pages: Value<ChildPages<*, Component>> = childPages(
        source = pagesNavigation,
        initialPages = {
            Pages(
                items = listOf(
                    View.Info,
                    View.CounterStrike,
                    View.RocketLeague,
                    View.Other
                ),
                selectedIndex = 0
            )
        }
    ) { config, componentContext ->
        createChild(config, componentContext)
    }

    @OptIn(ExperimentalDecomposeApi::class)
    override fun selectPage(index: Int) {
        pagesNavigation.select(index = index)
    }

    override val settingsVisible = MutableValue(false)

    private fun createChild(
        view: View,
        componentContext: ComponentContext
    ) : Component {
        return when (view) {
            is View.Info -> InfoViewComponent(
                componentContext,
                di,
                goToUser,
                goToSettings
            )
            is View.CounterStrike -> CounterStrikeViewComponent(
                componentContext,
                di
            )
            is View.RocketLeague -> RocketLeagueViewComponent(
                componentContext,
                di
            )
            is View.Other -> OtherScreenComponent(
                componentContext,
                di
            )
        }
    }

    @Composable
    override fun render() {
        HomeScreen(this)
    }

    override fun navigateToUser() {
        goToUser()
    }
}