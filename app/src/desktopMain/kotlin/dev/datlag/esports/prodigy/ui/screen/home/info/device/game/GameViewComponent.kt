package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.game.model.LocalGame
import dev.datlag.esports.prodigy.game.model.steam.User
import dev.datlag.esports.prodigy.model.UnsupportedUserChartGame
import dev.datlag.esports.prodigy.network.repository.CSStatsRepository
import dev.datlag.esports.prodigy.network.repository.SteamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import org.kodein.di.DI
import org.kodein.di.instance

class GameViewComponent(
    componentContext: ComponentContext,
    override val game: LocalGame,
    override val di: DI,
    private val back: () -> Unit
) : GameComponent, ComponentContext by componentContext {

    private val csStats: CSStatsRepository by di.instance()
    private val steamRepo: SteamRepository by di.instance()

    override val unsupportedUserChartGames: Collection<UnsupportedUserChartGame> = listOf(
        UnsupportedUserChartGame(
            steamId = "252950",
            name = "Rocket League",
            learnMoreUrl = "https://github.com/DatL4g/eSports-Prodigy"
        )
    )

    @Composable
    override fun render() {
        GameView(this)
    }

    override fun goBack() {
        back()
    }

    override fun loadUserAvatar(user: User): Flow<String> = steamRepo.userProfile(user.id).mapNotNull { it?.avatar }
}