package dev.datlag.esports.prodigy.model.state

import dev.datlag.esports.prodigy.model.octane.Event

sealed interface RequestState {
    data object Loading : RequestState
    data class Success(val events: List<Event>) : RequestState
    data class Error(val msg: String) : RequestState
}

sealed interface Action {
    data object RetryLoading : Action
}