package dev.datlag.esports.prodigy.ui.screen.home.counterstrike

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

class CounterStrikeViewComponent(
    componentContext: ComponentContext,
    override val di: DI
) : CounterStrikeComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        CounterStrikeView(this)
    }
}