package dev.datlag.esports.prodigy.ui.screen.home.info

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.datastore.common.updateCommented
import org.kodein.di.DI
import dev.datlag.esports.prodigy.ui.screen.home.info.device.DeviceViewComponent
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.instance

class InfoViewComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val goToUser: () -> Unit,
    private val goToSettings: (offset: Offset?) -> Unit
) : InfoComponent, ComponentContext by componentContext {

    override val deviceView = DeviceViewComponent(
        componentContext, di, goToUser, goToSettings
    )

    private val appSettings: DataStore<AppSettings> by di.instance()
    override val commented: Flow<Boolean> = appSettings.data.map { it.commented }

    @Composable
    override fun render() {
        InfoView(this)
    }

    override fun commented() {
        launchIO {
            appSettings.updateCommented(true)
        }
    }
}