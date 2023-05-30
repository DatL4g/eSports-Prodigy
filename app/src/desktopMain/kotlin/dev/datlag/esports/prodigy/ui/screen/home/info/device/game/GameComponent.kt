package dev.datlag.esports.prodigy.ui.screen.home.info.device.game

import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.ui.navigation.Component

interface GameComponent : Component {
    val game: Game

    fun goBack()
}