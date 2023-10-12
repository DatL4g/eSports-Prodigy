package dev.datlag.esports.prodigy.network.state.cs

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.model.state.Action
import dev.datlag.esports.prodigy.model.state.cs.NewsRequest
import dev.datlag.esports.prodigy.network.scraper.HLTVScraper
import io.ktor.client.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class HLTVNewsStateMachine(
    private val client: HttpClient,
    private val initialNews: Collection<News>?,
    private val saveNews: (Collection<News>) -> Unit
) : FlowReduxStateMachine<NewsRequest, Action>(initialState = NewsRequest.Loading(initialNews)) {
    init {
        spec {
            inState<NewsRequest.Loading> {
                onEnter { state ->
                    val result = HLTVScraper.scrapeNews(client)
                    if (result.isSuccess) {
                        state.override { NewsRequest.Success(result.getOrNull() ?: this.initial!!) }
                    } else {
                        state.override { NewsRequest.Error(result.exceptionOrNull()?.message ?: String()) }
                    }
                }
            }

            inState<NewsRequest.Success> {
                onEnterEffect { state ->
                    saveNews(state.news)
                }
            }

            inState<NewsRequest.Error> {
                on<Action.RetryLoading> { _, state ->
                    state.override { NewsRequest.Loading(initialNews) }
                }
            }
        }
    }
}