package dev.datlag.esports.prodigy.network.repository

import com.hadiyarajesh.flower_core.ApiErrorResponse
import com.hadiyarajesh.flower_core.ApiSuccessResponse
import com.hadiyarajesh.flower_core.Resource
import com.hadiyarajesh.flower_core.dbBoundResource
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.model.hltv.Team
import dev.datlag.esports.prodigy.network.Status
import dev.datlag.esports.prodigy.network.scraper.HLTVScraper
import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform

class HLTVRepository(
    private val client: HttpClient,
    private val initialHome: Home?,
    private val initialNews: List<News>?
) {

    val homeState: MutableStateFlow<Home?> = MutableStateFlow(initialHome)

    val newsState: MutableStateFlow<List<News>?> = MutableStateFlow(initialNews)
    val teamState: MutableStateFlow<Team?> = MutableStateFlow(null)

    private val _home: Flow<Resource<Home?>> by lazy {
        dbBoundResource(
            makeNetworkRequest = {
                val result = HLTVScraper.scrapeHome(client)
                if (result.isSuccess) {
                    ApiSuccessResponse(result.getOrNull()!!, emptySet())
                } else {
                    ApiErrorResponse(result.exceptionOrNull()?.message ?: String(), 0)
                }
            },
            fetchFromLocal = {
                homeState
            },
            shouldMakeNetworkRequest = {
                it == null
            },
            saveResponseData = {
                homeState.emit(it)
            }
        )
    }

    private val _news: Flow<Resource<List<News>>> by lazy {
        dbBoundResource(
            makeNetworkRequest = {
                val result = HLTVScraper.scrapeNews(client)
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

    private val _team: Flow<Resource<Team?>> by lazy {
        dbBoundResource(
            makeNetworkRequest = {
                val result = HLTVScraper.scrapeTeam(6667, client)
                if (result.isSuccess) {
                    ApiSuccessResponse(result.getOrNull(), emptySet())
                } else {
                    ApiErrorResponse(result.exceptionOrNull()?.message ?: String(), 0)
                }
            },
            fetchFromLocal = {
                teamState
            },
            shouldMakeNetworkRequest = {
                it == null
            },
            saveResponseData = {
                teamState.emit(it)
            }
        )
    }

    private val _homeStatus by lazy {
        _home.transform {
            return@transform emit(it.status)
        }
    }

    private val _newsStatus by lazy {
        _news.transform {
            return@transform emit(it.status)
        }
    }

    private val _teamStatus by lazy {
        _team.transform {
            return@transform emit(it.status)
        }
    }

    val homeStatus by lazy {
        _homeStatus.transform {
            return@transform emit(Status.create(it))
        }
    }

    val newsStatus by lazy {
        _newsStatus.transform {
            return@transform emit(Status.create(it))
        }
    }

    val teamStatus by lazy {
        _teamStatus.transform {
            return@transform emit(Status.create(it))
        }
    }

    val home by lazy {
        _homeStatus.transform {
            return@transform emit(when (it) {
                is Resource.Status.Loading -> {
                    it.data ?: homeState.value
                }
                is Resource.Status.EmptySuccess -> {
                    homeState.value
                }
                is Resource.Status.Success -> {
                    it.data
                }
                is Resource.Status.Error -> {
                    it.data ?: homeState.value
                }
            })
        }
    }

    val news by lazy {
        _newsStatus.transform {
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
                    it.data ?: newsState.value ?: emptyList()
                }
            })
        }
    }

    val team by lazy {
        _teamStatus.transform {
            return@transform emit(when (it) {
                is Resource.Status.Loading -> {
                    it.data
                }
                is Resource.Status.EmptySuccess -> {
                    teamState.value
                }
                is Resource.Status.Success -> {
                    it.data
                }
                is Resource.Status.Error -> {
                    it.data ?: teamState.value
                }
            })
        }
    }
}