package dev.datlag.esports.prodigy.ui.screen.settings

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

actual class SettingsScreenComponent actual constructor(
    componentContext: ComponentContext,
    override val di: DI,
    private val back: () -> Unit
) : SettingsComponent, ComponentContext by componentContext {

    override fun back() {
        this.back.invoke()
    }

    @Composable
    override fun render() {
        SettingsScreen(this)
    }
}