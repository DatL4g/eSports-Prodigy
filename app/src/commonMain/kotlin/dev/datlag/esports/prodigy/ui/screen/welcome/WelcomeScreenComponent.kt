package dev.datlag.esports.prodigy.ui.screen.welcome

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import org.kodein.di.DI

class WelcomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val goToHome: () -> Unit
) : WelcomeComponent, ComponentContext by componentContext {

    @Composable
    override fun render() {
        WelcomeScreen(this)
    }

    override fun finish() {
        goToHome()
    }
}