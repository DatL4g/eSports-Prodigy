package dev.datlag.esports.prodigy

import androidx.multidex.MultiDexApplication
import dev.datlag.esports.prodigy.module.NetworkModule
import dev.datlag.esports.prodigy.module.PlatformModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton

class App : MultiDexApplication(), DIAware {

    override val di: DI = DI {
        bindSingleton {
            applicationContext
        }

        import(NetworkModule.di)
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Napier.base(DebugAntilog())
        }
    }
}