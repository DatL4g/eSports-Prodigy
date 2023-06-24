package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import dev.datlag.esports.prodigy.common.ioDispatcher
import dev.datlag.esports.prodigy.common.ioScope
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.common.safeEmit
import dev.datlag.esports.prodigy.database.HLTVDB
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.model.hltv.Team
import dev.datlag.esports.prodigy.network.Status
import dev.datlag.esports.prodigy.network.repository.HLTVRepository
import kotlinx.coroutines.flow.*
import org.kodein.di.DI
import org.kodein.di.instance

class CounterStrikeViewComponent(
    componentContext: ComponentContext,
    override val di: DI
) : CounterStrikeComponent, ComponentContext by componentContext {

    private val scope = ioScope()
    private val hltvRepo: HLTVRepository by di.instance()
    private val db: HLTVDB by di.instance()

    override val home: Flow<Home?> = hltvRepo.home.flowOn(ioDispatcher())
    override val homeStatus: Flow<Status> = hltvRepo.homeStatus.flowOn(ioDispatcher())

    @Composable
    override fun render() {
        CounterStrikeView(this)
    }

    companion object {
        private const val NEWS_STATE = "NEWS_STATE"
    }
}