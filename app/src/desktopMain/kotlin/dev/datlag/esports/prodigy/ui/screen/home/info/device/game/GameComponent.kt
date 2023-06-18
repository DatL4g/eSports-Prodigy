package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import com.netguru.multiplatform.charts.line.LineChartData
import dev.datlag.esports.prodigy.game.model.LocalGame
import dev.datlag.esports.prodigy.model.UnsupportedUserChartGame
import dev.datlag.esports.prodigy.ui.navigation.Component

interface GameComponent : Component {

    val game: LocalGame
    val unsupportedUserChartGames: Collection<UnsupportedUserChartGame>

    fun goBack()
}