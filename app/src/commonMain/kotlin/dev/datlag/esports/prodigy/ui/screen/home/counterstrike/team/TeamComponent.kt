package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.team

import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.hltv.Team
import dev.datlag.esports.prodigy.model.state.cs.TeamRequest
import dev.datlag.esports.prodigy.network.state.cs.HLTVTeamStateMachine
import dev.datlag.esports.prodigy.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface TeamComponent : Component {

    val initialTeam: Home.Team
    val teamState: Flow<TeamRequest>

    fun back()
}