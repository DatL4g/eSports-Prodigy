package dev.datlag.esports.prodigy.ui.screen.home.info

import dev.datlag.esports.prodigy.ui.navigation.Component
import dev.datlag.esports.prodigy.ui.screen.home.info.device.DeviceViewComponent

interface InfoComponent : Component {
    val deviceView: DeviceViewComponent
}