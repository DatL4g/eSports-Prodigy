package dev.datlag.esports.prodigy.model.state.cs

import dev.datlag.esports.prodigy.model.hltv.Home

sealed interface HomeRequest {

    data class Loading(val initial: Home?) : HomeRequest
    data class Success(val home: Home) : HomeRequest
    data class Error(val msg: String) : HomeRequest
}