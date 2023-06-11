package dev.datlag.esports.prodigy.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.common.mainScope
import dev.datlag.esports.prodigy.datastore.common.updateAppearance
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings
import dev.datlag.esports.prodigy.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.DI
import org.kodein.di.instance

actual class SettingsScreenComponent actual constructor(
    componentContext: ComponentContext,
    override val di: DI,
    private val back: () -> Unit
) : SettingsComponent, ComponentContext by componentContext {

    private val scope = mainScope()
    private val appSettings: DataStore<AppSettings> by di.instance()
    override val themeMode: Flow<ThemeMode> = appSettings.data.map { it.appearance.themeMode }.map {
        ThemeMode.ofValue(it)
    }

    override fun back() {
        this.back.invoke()
    }

    @Composable
    override fun render() {
        SettingsScreen(this)
    }

    override fun changeThemeMode(mode: ThemeMode) {
        scope.launchIO {
            appSettings.updateAppearance(
                themeMode = mode.saveValue
            )
        }
    }
}