package dev.datlag.esports.prodigy.ui.screen.user

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import org.kodein.di.DI

actual class UserScreenComponent actual constructor(
    componentContext: ComponentContext,
    override val di: DI,
    private val back: () -> Unit
) : UserComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        UserScreen(this)
    }

    override fun back() {
        this.back.invoke()
    }
}