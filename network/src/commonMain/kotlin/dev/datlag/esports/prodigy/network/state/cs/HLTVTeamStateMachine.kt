package dev.datlag.esports.prodigy.network.state.cs

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.state.cs.TeamAction
import dev.datlag.esports.prodigy.model.state.cs.TeamRequest
import dev.datlag.esports.prodigy.network.scraper.HLTVScraper
import io.ktor.client.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.properties.Delegates

@OptIn(ExperimentalCoroutinesApi::class)
class HLTVTeamStateMachine(
    private val client: HttpClient,
    private val initialTeam: Home.Team
) : FlowReduxStateMachine<TeamRequest, TeamAction>(TeamRequest.Loading(initialTeam.href, initialTeam.id)) {

    private var lastHref by Delegates.notNull<String>()
    private var lastId by Delegates.notNull<Number>()

    init {
        spec {
            inState<TeamRequest.Loading> {
                onEnter { state ->
                    lastHref = state.snapshot.href
                    lastId = state.snapshot.id

                    val result = HLTVScraper.scrapeTeam(state.snapshot.href, state.snapshot.id, client)
                    if (result.isSuccess) {
                        state.override { TeamRequest.Success(result.getOrThrow()) }
                    } else {
                        state.override { TeamRequest.Error(result.exceptionOrNull()?.message ?: String()) }
                    }
                }
            }

            inState<TeamRequest.Error> {
                on<TeamAction.RetryLoading> { _, state ->
                    state.override { TeamRequest.Loading(lastHref, lastId) }
                }
            }
        }
    }
}