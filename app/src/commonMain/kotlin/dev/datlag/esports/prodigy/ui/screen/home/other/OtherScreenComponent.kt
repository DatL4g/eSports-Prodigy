package dev.datlag.esports.prodigy.ui.screen.home.other

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import dev.datlag.esports.prodigy.ui.navigation.Component
import org.kodein.di.DI

class OtherScreenComponent(
    componentContext: ComponentContext,
    override val di: DI
) : Component {

    @Composable
    override fun render() {
        OtherScreen()
    }
}