package dev.datlag.esports.prodigy.ui.screen.home.rocketleague

import dev.datlag.esports.prodigy.model.state.RequestState
import dev.datlag.esports.prodigy.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface RocketLeagueComponent : Component {
    val eventsRequestState: Flow<RequestState>

    fun retryLoadingEvents()
}