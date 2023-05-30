package dev.datlag.esports.prodigy.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import dev.datlag.esports.prodigy.App
import dev.datlag.esports.prodigy.ui.LocalWindowSize
import dev.datlag.esports.prodigy.ui.App
import dev.datlag.esports.prodigy.ui.LocalOrientation
import dev.datlag.esports.prodigy.ui.Orientation
import dev.datlag.esports.prodigy.ui.navigation.NavHostComponent
import okio.Path.Companion.toOkioPath
import dev.datlag.esports.prodigy.R
import dev.datlag.esports.prodigy.ui.WindowSize
import dev.datlag.esports.prodigy.common.basedOnWidth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            this.setTheme(R.style.AppTheme)
        } else {
            installSplashScreen()
        }
        super.onCreate(savedInstanceState)

        val di = ((applicationContext as? App) ?: (application as App)).di
        val imageLoader = ImageLoader {
            components {
                setupDefaultComponents(context = this@MainActivity)
            }
            interceptor {
                memoryCacheConfig {
                    maxSizePercent(this@MainActivity, 0.25)
                }
                diskCacheConfig {
                    directory(this@MainActivity.cacheDir.resolve("imageCache").toOkioPath())
                    maxSizeBytes(256L * 1024 * 1024)
                }
            }
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
                LocalImageLoader provides imageLoader,
                LocalOrientation provides orientation,
                LocalWindowSize provides WindowSize.basedOnWidth(this)
            ) {
                App(di) {
                    root.render()
                }
            }
        }
    }
}