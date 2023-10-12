package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.news

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.common.ioDispatcher
import dev.datlag.esports.prodigy.model.state.cs.NewsRequest
import dev.datlag.esports.prodigy.network.state.cs.HLTVNewsStateMachine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.kodein.di.DI
import org.kodein.di.instance

class NewsViewComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onBack: () -> Unit
) : NewsComponent, ComponentContext by componentContext {

    private val newsStateMachine by di.instance<HLTVNewsStateMachine>()
    override val newsState: Flow<NewsRequest> = newsStateMachine.state.flowOn(ioDispatcher())

    @Composable
    override fun render() {
        NewsView(this)
    }

    override fun back() {
        onBack()
    }
}