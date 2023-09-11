package dev.datlag.esports.prodigy.ui.screen.home.info.device

import androidx.compose.ui.geometry.Offset
import dev.datlag.esports.prodigy.ui.navigation.Component

actual interface DeviceComponent : Component {

    actual fun navigateToUser()

    actual fun navigateToSettings(offset: Offset?)
}