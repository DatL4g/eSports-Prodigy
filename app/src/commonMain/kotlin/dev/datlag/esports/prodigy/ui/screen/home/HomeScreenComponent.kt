package dev.datlag.esports.prodigy.ui.screen.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pending
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
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

class HomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI
) : HomeComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<View>()
    private val _childStack = childStack(
        source = navigation,
        initialConfiguration = View.Info,
        handleBackButton = true,
        childFactory = ::createChild
    )
    override val childStack: Value<ChildStack<*, Component>> = _childStack
    private val _selectedPage: MutableValue<Int> = MutableValue(0)
    override val selectedPage: Value<Int> = _selectedPage

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

    init {
        selectedPage.observe(lifecycle) {
            when (it) {
                0 -> navigation.replaceCurrent(View.Info)
                1 -> navigation.replaceCurrent(View.CounterStrike)
            }
        }
    }

    private fun createChild(
        view: View,
        componentContext: ComponentContext
    ) : Component {
        return when (view) {
            is View.Info -> InfoViewComponent(
                componentContext,
                di
            )
            is View.CounterStrike -> CounterStrikeViewComponent(
                componentContext,
                di
            )
        }
    }

    @Composable
    override fun render() {
        HomeScreen(this)
    }

    override fun navigate(key: Int) {
        _selectedPage.value = key
    }
}