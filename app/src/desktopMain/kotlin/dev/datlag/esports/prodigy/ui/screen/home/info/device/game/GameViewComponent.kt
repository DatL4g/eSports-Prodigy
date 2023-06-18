package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.ComponentContext
import com.netguru.multiplatform.charts.line.LineChartData
import com.netguru.multiplatform.charts.line.LineChartPoint
import com.netguru.multiplatform.charts.line.LineChartSeries
import dev.datlag.esports.prodigy.game.model.LocalGame
import dev.datlag.esports.prodigy.model.UnsupportedUserChartGame
import dev.datlag.esports.prodigy.model.common.asList
import kotlinx.datetime.Clock
import org.kodein.di.DI
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class GameViewComponent(
    componentContext: ComponentContext,
    override val game: LocalGame,
    override val di: DI,
    private val back: () -> Unit
) : GameComponent, ComponentContext by componentContext {

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
}