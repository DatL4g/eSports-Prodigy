package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import dev.datlag.esports.prodigy.common.ioDispatcher
import dev.datlag.esports.prodigy.common.ioScope
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.common.safeEmit
import dev.datlag.esports.prodigy.database.HLTVDB
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.model.hltv.Team
import dev.datlag.esports.prodigy.network.Status
import dev.datlag.esports.prodigy.network.repository.HLTVRepository
import dev.datlag.esports.prodigy.ui.screen.home.counterstrike.team.TeamViewComponent
import kotlinx.coroutines.flow.*
import org.kodein.di.DI
import org.kodein.di.instance

class CounterStrikeViewComponent(
    componentContext: ComponentContext,
    override val di: DI
) : CounterStrikeComponent, ComponentContext by componentContext {

    private val scope = ioScope()
    private val hltvRepo: HLTVRepository by di.instance()
    private val db: HLTVDB by di.instance()

    override val home: Flow<Home?> = hltvRepo.home.flowOn(ioDispatcher())
    override val homeStatus: Flow<Status> = hltvRepo.homeStatus.flowOn(ioDispatcher())

    private val navigation = SlotNavigation<CounterStrikeConfig>()
    private val _child = childSlot(
        source = navigation,
        initialConfiguration = {
            CounterStrikeConfig.EMPTY
        },
        handleBackButton = true
    ) { config, componentContext ->
        when (config) {
            is CounterStrikeConfig.Team -> TeamViewComponent(
                componentContext,
                config.initialTeam,
                di
            )
            else -> config
        }
    }

    override val child: Value<ChildSlot<CounterStrikeConfig, Any>> = _child

    @Composable
    override fun render() {
        CounterStrikeView(this)
    }

    override fun teamClicked(team: Home.Team) {
        navigation.activate(CounterStrikeConfig.Team(team))
    }

    companion object {
        private const val NEWS_STATE = "NEWS_STATE"
    }
}