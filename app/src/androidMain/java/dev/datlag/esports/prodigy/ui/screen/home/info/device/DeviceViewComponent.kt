package dev.datlag.esports.prodigy.ui.screen.home.info.device

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

actual class DeviceViewComponent actual constructor(
    componentContext: ComponentContext,
    override val di: DI
) : DeviceComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        DeviceView(this)
    }
}