package dev.datlag.esports.prodigy.ui.screen.home.info.device

import androidx.compose.ui.geometry.Offset
import dev.datlag.esports.prodigy.ui.navigation.Component

expect interface DeviceComponent : Component {
    fun navigateToUser()

    fun navigateToSettings(offset: Offset?)
}