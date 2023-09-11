package dev.datlag.esports.prodigy.ui.screen.home.counterstrike.article

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.common.ioDispatcher
import dev.datlag.esports.prodigy.network.repository.WebRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import org.kodein.di.DI
import org.kodein.di.instance

class ArticleViewComponent(
    componentContext: ComponentContext,
    private val href: String,
    override val di: DI,
    private val onBack: () -> Unit
) : ArticleComponent, ComponentContext by componentContext {

    private val webRepository: WebRepository by di.instance()
    override val content: Flow<String?> = webRepository.skrapeAsMarkdown(href).flowOn(ioDispatcher())

    @Composable
    override fun render() {
        ArticleView(this)
    }

    override fun back() {
        onBack()
    }
}