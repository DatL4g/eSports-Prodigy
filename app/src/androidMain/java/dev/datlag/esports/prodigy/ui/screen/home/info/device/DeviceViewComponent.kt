package dev.datlag.esports.prodigy.ui.screen.home.info.device

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

actual class DeviceViewComponent actual constructor(
    componentContext: ComponentContext,
    override val di: DI,
    private val goToUser: () -> Unit,
    private val goToSettings: () -> Unit
) : DeviceComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        DeviceView(this)
    }

    override fun navigateToUser() {
        goToUser()
    }

    override fun navigateToSettings() {
        goToSettings()
    }
}