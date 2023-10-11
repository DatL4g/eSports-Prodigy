package dev.datlag.esports.prodigy.network.state.cs

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.state.Action
import dev.datlag.esports.prodigy.model.state.cs.HomeRequest
import dev.datlag.esports.prodigy.network.scraper.HLTVScraper
import io.ktor.client.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class HLTVHomeStateMachine(
    private val client: HttpClient,
    private val initialHome: Home?
) : FlowReduxStateMachine<HomeRequest, Action>(initialState = HomeRequest.Loading(initialHome)) {
    init {
        spec {
            inState<HomeRequest.Loading> {
                onEnter { state ->
                    val result = HLTVScraper.scrapeHome(client)
                    if (result.isSuccess) {
                        state.override { HomeRequest.Success(result.getOrNull() ?: this.initial!!) }
                    } else {
                        state.override { HomeRequest.Error(result.exceptionOrNull()?.message ?: String()) }
                    }
                }
            }

            inState<HomeRequest.Error> {
                on<Action.RetryLoading> { _, state ->
                    state.override { HomeRequest.Loading(initialHome) }
                }
            }
        }
    }
}