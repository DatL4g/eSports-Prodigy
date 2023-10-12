package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.model.hltv.Team
import dev.datlag.esports.prodigy.model.state.cs.HomeRequest
import dev.datlag.esports.prodigy.network.Status
import dev.datlag.esports.prodigy.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface CounterStrikeComponent : Component {

    val homeRequestState: Flow<HomeRequest>
    val child: Value<ChildSlot<CounterStrikeConfig, Component>>

    fun teamClicked(team: Home.Team)
    fun articleClicked(href: String)

    fun newsClicked()

}