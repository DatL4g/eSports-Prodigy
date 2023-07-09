package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import com.netguru.multiplatform.charts.line.LineChartData
import dev.datlag.esports.prodigy.game.model.LocalGame
import dev.datlag.esports.prodigy.game.model.steam.User
import dev.datlag.esports.prodigy.model.UnsupportedUserChartGame
import dev.datlag.esports.prodigy.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

interface GameComponent : Component {

    val game: LocalGame
    val unsupportedUserChartGames: Collection<UnsupportedUserChartGame>

    fun goBack()

    fun loadUserAvatar(user: User): Flow<String>
}