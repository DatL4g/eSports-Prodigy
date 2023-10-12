package dev.datlag.esports.prodigy.model.state.cs

import dev.datlag.esports.prodigy.model.hltv.News

sealed interface NewsRequest {
    data class Loading(val initial: Collection<News>?) : NewsRequest
    data class Success(val news: Collection<News>) : NewsRequest
    data class Error(val msg: String) : NewsRequest
}