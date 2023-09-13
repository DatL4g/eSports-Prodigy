package dev.datlag.esports.prodigy.ui.screen.home.rocketleague

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.common.ioDispatcher
import dev.datlag.esports.prodigy.common.ioScope
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.model.state.Action
import dev.datlag.esports.prodigy.model.state.RequestState
import dev.datlag.esports.prodigy.network.state.OctaneEventsStateMachine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.kodein.di.DI
import org.kodein.di.instance

class RocketLeagueViewComponent(
    componentContext: ComponentContext,
    override val di: DI
) : RocketLeagueComponent, ComponentContext by componentContext {

    private val scope = ioScope()
    private val eventsStateMachine by di.instance<OctaneEventsStateMachine>()

    override val eventsRequestState: Flow<RequestState> = eventsStateMachine.state.flowOn(ioDispatcher())

    @Composable
    override fun render() {
        RocketLeagueView(this)
    }

    override fun retryLoadingEvents() {
        scope.launchIO {
            eventsStateMachine.dispatch(Action.RetryLoading)
        }
    }
}