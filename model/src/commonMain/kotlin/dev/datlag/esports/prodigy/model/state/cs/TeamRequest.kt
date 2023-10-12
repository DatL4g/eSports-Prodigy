package dev.datlag.esports.prodigy.model.state.cs

import dev.datlag.esports.prodigy.model.hltv.Team

sealed interface TeamRequest {
    data class Loading(val href: String, val id: Number) : TeamRequest
    data class Success(val team: Team) : TeamRequest
    data class Error(val msg: String) : TeamRequest
}

sealed interface TeamAction {
    data object RetryLoading : TeamAction
}