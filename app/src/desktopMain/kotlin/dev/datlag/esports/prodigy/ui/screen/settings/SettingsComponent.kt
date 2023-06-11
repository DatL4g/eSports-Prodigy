package dev.datlag.esports.prodigy.ui.screen.settings

import dev.datlag.esports.prodigy.model.ThemeMode
import dev.datlag.esports.prodigy.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

actual interface SettingsComponent : Component {

    val themeMode: Flow<ThemeMode>

    actual fun back()

    fun changeThemeMode(mode: ThemeMode)

}