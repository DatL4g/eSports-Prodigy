package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.model.hltv.Team
import dev.datlag.esports.prodigy.network.Status
import dev.datlag.esports.prodigy.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface CounterStrikeComponent : Component {

    val home: Flow<Home?>
    val homeStatus: Flow<Status>

}