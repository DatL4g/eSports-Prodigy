package dev.datlag.esports.prodigy.ui.screen.home.info.device

import androidx.compose.ui.geometry.Offset
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import dev.datlag.esports.prodigy.game.model.LocalGame
import dev.datlag.esports.prodigy.game.model.LocalGameInfo
import dev.datlag.esports.prodigy.ui.navigation.Component
import dev.datlag.esports.prodigy.ui.screen.home.info.device.game.GameConfig
import kotlinx.coroutines.flow.Flow
import java.awt.Image

actual interface DeviceComponent : Component {
    val child: Value<ChildSlot<GameConfig, Component>>

    val games: Flow<List<LocalGame>>

    fun gameClicked(game: LocalGame)

    actual fun navigateToUser()

    actual fun navigateToSettings(offset: Offset?)
}