package dev.datlag.esports.prodigy.ui.screen.welcome

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.datlag.esports.prodigy.common.launchIO
import dev.datlag.esports.prodigy.common.mainScope
import dev.datlag.esports.prodigy.common.withMainContext
import dev.datlag.esports.prodigy.datastore.common.updateWelcomed
import org.kodein.di.DI
import org.kodein.di.instance
import dev.datlag.esports.prodigy.datastore.preferences.AppSettings

class WelcomeScreenComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val goToHome: () -> Unit
) : WelcomeComponent, ComponentContext by componentContext {

    private val appSettings: DataStore<AppSettings> by di.instance()

    @Composable
    override fun render() {
        WelcomeScreen(this)
    }

    override fun finish() {
        launchIO {
            appSettings.updateWelcomed(true)
            withMainContext {
                goToHome()
            }
        }
    }
}