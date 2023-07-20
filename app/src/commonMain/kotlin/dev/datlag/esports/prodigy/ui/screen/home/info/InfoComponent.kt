package dev.datlag.esports.prodigy.ui.screen.home.info

import dev.datlag.esports.prodigy.ui.navigation.Component
import dev.datlag.esports.prodigy.ui.screen.home.info.device.DeviceViewComponent
import kotlinx.coroutines.flow.Flow

interface InfoComponent : Component {
    val deviceView: DeviceViewComponent
    val commented: Flow<Boolean>

    fun commented()
}