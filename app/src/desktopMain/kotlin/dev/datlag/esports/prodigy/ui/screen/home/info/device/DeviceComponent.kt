package dev.datlag.esports.prodigy.ui.screen.home.info.device

import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.value.Value
import dev.datlag.esports.prodigy.game.model.Game
import dev.datlag.esports.prodigy.ui.navigation.Component
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameConfig
import kotlinx.coroutines.flow.Flow

actual interface DeviceComponent : Component {
    val gameManifests: Flow<List<Game>>
    val child: Value<ChildOverlay<GameConfig, Any>>

    fun gameClicked(game: Game)
}