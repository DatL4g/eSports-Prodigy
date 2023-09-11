package dev.datlag.esports.prodigy.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.savedstate.SavedStateRegistryOwner
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.essenty.backhandler.backHandler
import com.arkivanov.essenty.instancekeeper.instanceKeeper
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import com.arkivanov.essenty.parcelable.ParcelableContainer
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import com.arkivanov.essenty.statekeeper.stateKeeper
import dev.datlag.esports.prodigy.App
import dev.datlag.esports.prodigy.ui.navigation.NavHostComponent
import dev.datlag.esports.prodigy.R
import dev.datlag.esports.prodigy.common.getSafeParcelable
import dev.datlag.esports.prodigy.common.getSizeInBytes
import dev.datlag.esports.prodigy.other.Commonizer
import dev.datlag.esports.prodigy.other.StateSaver
import dev.datlag.esports.prodigy.ui.*
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.takeFrom
import io.kamel.image.config.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            this.setTheme(R.style.AppTheme)
        } else {
            installSplashScreen()
        }
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val di = ((applicationContext as? App) ?: (application as App)).di
        val imageConfig = KamelConfig {
            takeFrom(KamelConfig.Default)
            resourcesFetcher(this@MainActivity)
            resourcesIdMapper(this@MainActivity)
            imageVectorDecoder()
            svgDecoder()
        }

        val lifecycleOwner = object : LifecycleOwner {
            override val lifecycle: Lifecycle = essentyLifecycle()
        }
        val root = NavHostComponent.create(
            componentContext = DefaultComponentContext(
                lifecycle = lifecycleOwner.lifecycle,
                stateKeeper = stateKeeper(onBundleTooLarge = {
                    StateSaver.state[KEY_STATE] = it
                }),
                instanceKeeper = instanceKeeper(),
                backHandler = backHandler()
            ),
            di = di
        )

        setContent {
            val configuration = LocalConfiguration.current
            val orientation = when (configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> Orientation.LANDSCAPE
                else -> Orientation.PORTRAIT
            }
            CompositionLocalProvider(
                LocalKamelConfig provides imageConfig,
                LocalOrientation provides orientation,
                LocalCommonizer provides Commonizer(this),
                LocalLifecycleOwner provides lifecycleOwner
            ) {
                App(di) {
                    root.render()
                }
            }
        }
    }

    private fun SavedStateRegistryOwner.stateKeeper(onBundleTooLarge: (ParcelableContainer) -> Unit = { }): StateKeeper {
        val dispatcher = StateKeeperDispatcher(
            savedStateRegistry.consumeRestoredStateForKey(KEY_STATE)?.getSafeParcelable(KEY_STATE) ?: StateSaver.state[KEY_STATE]
        )

        savedStateRegistry.registerSavedStateProvider(KEY_STATE) {
            val savedState = dispatcher.save()
            val bundle = Bundle()

            if (savedState.getSizeInBytes() <= SAVED_STATE_MAX_SIZE) {
                bundle.putParcelable(KEY_STATE, savedState)
            } else {
                onBundleTooLarge(savedState)
            }

            bundle
        }

        return dispatcher
    }

    companion object {
        private const val KEY_STATE = "STATE_KEEPER_STATE"
        private const val SAVED_STATE_MAX_SIZE = 500_000
    }
}