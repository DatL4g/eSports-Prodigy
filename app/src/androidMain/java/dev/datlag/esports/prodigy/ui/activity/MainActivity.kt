package dev.datlag.esports.prodigy.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import dev.datlag.esports.prodigy.App
import dev.datlag.esports.prodigy.ui.navigation.NavHostComponent
import okio.Path.Companion.toOkioPath
import dev.datlag.esports.prodigy.R
import dev.datlag.esports.prodigy.common.basedOnWidth
import dev.datlag.esports.prodigy.other.Commonizer
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

        val di = ((applicationContext as? App) ?: (application as App)).di
        val imageConfig = KamelConfig {
            takeFrom(KamelConfig.Default)
            resourcesFetcher(this@MainActivity)
            resourcesIdMapper(this@MainActivity)
            imageVectorDecoder()
            svgDecoder()
        }
        val root = NavHostComponent.create(
            componentContext = defaultComponentContext(),
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
                LocalWindowSize provides WindowSize.basedOnWidth(this),
                LocalCommonizer provides Commonizer(this)
            ) {
                App(di) {
                    root.render()
                }
            }
        }
    }
}