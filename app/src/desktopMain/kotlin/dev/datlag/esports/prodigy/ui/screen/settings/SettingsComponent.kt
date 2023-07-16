package dev.datlag.esports.prodigy.ui.screen.settings

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import dev.datlag.esports.prodigy.model.ThemeMode
import dev.datlag.esports.prodigy.ui.dialog.DialogComponent
import dev.datlag.esports.prodigy.ui.navigation.Component
import kotlinx.coroutines.flow.Flow

actual interface SettingsComponent : Component {

    val themeMode: Flow<ThemeMode>
    val contentColors: Flow<Boolean>
    val dialog: Value<ChildSlot<DialogConfig, DialogComponent>>

    actual fun back()

    fun changeThemeMode(mode: ThemeMode)

    fun changeContentColors(enabled: Boolean)

    fun showDialog(config: DialogConfig)

}