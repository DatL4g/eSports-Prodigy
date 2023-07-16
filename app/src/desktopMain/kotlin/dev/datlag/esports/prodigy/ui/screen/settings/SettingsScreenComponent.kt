package dev.datlag.esports.prodigy.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.common.mainScope
import dev.datlag.esports.prodigy.datastore.common.updateAppearance
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings
import dev.datlag.esports.prodigy.model.ThemeMode
import dev.datlag.esports.prodigy.model.common.deleteSafely
import dev.datlag.esports.prodigy.model.common.suffix
import dev.datlag.esports.prodigy.ui.dialog.DialogComponent
import dev.datlag.esports.prodigy.ui.screen.settings.dialog.SteamFinderDialogComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.File

actual class SettingsScreenComponent actual constructor(
    componentContext: ComponentContext,
    override val di: DI,
    private val back: () -> Unit
) : SettingsComponent, ComponentContext by componentContext {

    private val scope = mainScope()
    private val appSettings: DataStore<AppSettings> by di.instance()
    private val appSettingsFile: File by di.instance("AppSettingsFile")
    private val appSettingsTempFile = appSettingsFile.suffix(".tmp")
    override val themeMode: Flow<ThemeMode> = appSettings.data.map { it.appearance.themeMode }.map {
        ThemeMode.ofValue(it)
    }

    private val dialogNavigation = SlotNavigation<DialogConfig>()
    private val _dialog = childSlot(
        source = dialogNavigation
    ) { config, componentContext ->
        when (config) {
            is DialogConfig.SteamFinder -> SteamFinderDialogComponent(
                componentContext = componentContext,
                onDismissed = dialogNavigation::dismiss,
                di = di
            )
        }
    }
    override val dialog: Value<ChildSlot<DialogConfig, DialogComponent>> = _dialog

    override fun back() {
        this.back.invoke()
    }

    @Composable
    override fun render() {
        SettingsScreen(this)
    }

    override fun changeThemeMode(mode: ThemeMode) {
        scope.launchIO {
            appSettingsTempFile.deleteSafely()
            appSettings.updateAppearance(
                themeMode = mode.saveValue
            )
        }
    }

    override fun showDialog(config: DialogConfig) {
        dialogNavigation.activate(config)
    }
}