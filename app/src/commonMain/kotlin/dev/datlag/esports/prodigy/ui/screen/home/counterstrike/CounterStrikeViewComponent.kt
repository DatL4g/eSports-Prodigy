package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.common.ioDispatcher
import dev.datlag.esports.prodigy.common.ioScope
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.network.Status
import dev.datlag.esports.prodigy.network.repository.HLTVRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.kodein.di.DI
import org.kodein.di.instance

class CounterStrikeViewComponent(
    componentContext: ComponentContext,
    override val di: DI
) : CounterStrikeComponent, ComponentContext by componentContext {

    private val hltvRepo: HLTVRepository by di.instance()

    override val news: Flow<List<News>> = hltvRepo.news.flowOn(ioDispatcher())
    override val newsStatus: Flow<Status> = hltvRepo.status.flowOn(ioDispatcher())

    @Composable
    override fun render() {
        CounterStrikeView(this)
    }
}