package dev.datlag.esports.prodigy.network.repository

import com.hadiyarajesh.flower_core.ApiErrorResponse
import com.hadiyarajesh.flower_core.ApiSuccessResponse
import com.hadiyarajesh.flower_core.Resource
import com.hadiyarajesh.flower_core.dbBoundResource
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.network.Status
import dev.datlag.esports.prodigy.network.fetcher.KtorFetcher
import io.ktor.client.*
import io.ktor.client.request.*
import it.skrape.core.document
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extract
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform

class HLTVRepository(
    private val client: HttpClient
) {

    private val newsState: MutableStateFlow<List<News>?> = MutableStateFlow(null)

    private val _news: Flow<Resource<List<News>>> by lazy {
        dbBoundResource(
            makeNetworkRequest = {
                val result = loadFreshNews()
                if (result.isSuccess) {
                    ApiSuccessResponse(result.getOrNull() ?: emptyList(), emptySet())
                } else {
                    ApiErrorResponse(result.exceptionOrNull()?.message ?: String(), 0)
                }
            },
            fetchFromLocal = {
                flowOf(newsState.value ?: emptyList())
            },
            shouldMakeNetworkRequest = {
                it.isNullOrEmpty()
            },
            saveResponseData = {
                newsState.emit(it)
            }
        )
    }

    private val _status by lazy {
        _news.transform {
            return@transform emit(it.status)
        }
    }

    val status by lazy {
        _status.transform {
            return@transform emit(Status.create(it))
        }
    }

    val news by lazy {
        _status.transform {
            return@transform emit(when (it) {
                is Resource.Status.Loading -> {
                    it.data ?: emptyList()
                }
                is Resource.Status.EmptySuccess -> {
                    newsState.value ?: emptyList()
                }
                is Resource.Status.Success -> {
                    it.data
                }
                is Resource.Status.Error -> {
                    it.data ?: emptyList()
                }
            })
        }
    }

    suspend fun loadFreshNews(): Result<List<News>> {
        return skrape(KtorFetcher(client)) {
            request {
                url("https://www.hltv.org/news/archive")
            }
            response {
                if (responseStatus.code != 200) {
                    Result.failure(Exception(responseStatus.message))
                } else {
                    Result.success(
                        document.findAll(".article").map { element ->
                            val newsFlag = element.findFirst(".newsflag")
                            News(
                                link = element.attribute("href"),
                                title = element.findFirst(".newstext").text,
                                date = element.findFirst(".newsrecent").text,
                                country = News.Country(
                                    name = newsFlag.attribute("alt"),
                                    code = newsFlag.attribute("src").split('/').last().split('.').first()
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}