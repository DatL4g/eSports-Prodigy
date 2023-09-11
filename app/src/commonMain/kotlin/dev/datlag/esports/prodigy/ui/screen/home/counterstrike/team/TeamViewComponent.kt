package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.team

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.common.ioDispatcher
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.hltv.Team
import dev.datlag.esports.prodigy.network.repository.HLTVRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.kodein.di.DI
import org.kodein.di.instance

class TeamViewComponent(
    componentContext: ComponentContext,
    override val initialTeam: Home.Team,
    override val di: DI,
    private val onBack: () -> Unit
) : TeamComponent, ComponentContext by componentContext {

    private val hltvRepository: HLTVRepository by di.instance()
    override val team: Flow<Team?> = hltvRepository.team(initialTeam.href, initialTeam.id).flowOn(ioDispatcher())

    @Composable
    override fun render() {
        TeamView(this)
    }

    override fun back() {
        onBack()
    }
}