package dev.datlag.esports.prodigy.ui.screen.home.info.device

import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.value.Value
import dev.datlag.esports.prodigy.game.model.LocalGame
import dev.datlag.esports.prodigy.game.model.LocalGameInfo
import dev.datlag.esports.prodigy.ui.navigation.Component
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameConfig
import kotlinx.coroutines.flow.Flow
import java.awt.Image

actual interface DeviceComponent : Component {
    val child: Value<ChildOverlay<GameConfig, Any>>

    val games: Flow<List<LocalGame>>

    fun gameClicked(game: LocalGame)
    fun loadSchemeFor(gameTitle: String, image: Image)

    actual fun navigateToUser()

    actual fun navigateToSettings()
}