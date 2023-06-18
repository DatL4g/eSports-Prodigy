package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.network.Status
import dev.datlag.esports.prodigy.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface CounterStrikeComponent : Component {

    val news: Flow<List<News>>
    val newsStatus: Flow<Status>

}