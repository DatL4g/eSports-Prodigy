package dev.datlag.esports.prodigy.ui.screen.home.info.device

import androidx.compose.ui.geometry.Offset
import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

expect class DeviceViewComponent(
    componentContext: ComponentContext,
    di: DI,
    goToUser: () -> Unit,
    goToSettings: (offset: Offset?) -> Unit,
) : DeviceComponent