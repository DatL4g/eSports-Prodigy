package dev.datlag.esports.prodigy.network.state

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.esports.prodigy.model.octane.Events
import dev.datlag.esports.prodigy.model.state.Action
import dev.datlag.esports.prodigy.model.state.RequestState
import dev.datlag.esports.prodigy.network.OctaneAPI
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class OctaneEventsStateMachine(
    private val api: OctaneAPI
) : FlowReduxStateMachine<RequestState, Action>(initialState = RequestState.Loading) {
    init {
        spec {
            inState<RequestState.Loading> {
                onEnter { state ->
                    try {
                        val loadedEvents = api.events().events
                        state.override { RequestState.Success(loadedEvents) }
                    } catch (t: Throwable) {
                        state.override { RequestState.Error(t.message ?: String()) }
                    }
                }
            }

            inState<RequestState.Error> {
                on<Action.RetryLoading> { _, state ->
                    state.override { RequestState.Loading }
                }
            }
        }
    }
}