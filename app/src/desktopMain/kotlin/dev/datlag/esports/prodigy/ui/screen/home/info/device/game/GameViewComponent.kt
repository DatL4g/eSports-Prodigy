package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.game.model.Game
import org.kodein.di.DI

class GameViewComponent(
    componentContext: ComponentContext,
    override val game: Game,
    override val di: DI,
    private val back: () -> Unit
) : GameComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        GameView(this)
    }

    override fun goBack() {
        back()
    }
}