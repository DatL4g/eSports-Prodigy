package dev.datlag.esports.prodigy.module

import android.content.Context
import androidx.datastore.dataStoreFile
import dev.datlag.esports.prodigy.database.DriverFactory
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

actual object PlatformModule {

    private const val NAME = "PlatformModuleAndroid"

    actual val di = DI.Module(NAME) {
        bindSingleton("AppSettingsFile") {
            val app: Context = instance()
            app.dataStoreFile("AppSettings.pb")
        }

        bindSingleton {
            DriverFactory(instance())
        }
        bindSingleton("HLTVDriver") {
            instance<DriverFactory>().createHLTVDriver()
        }
        bindSingleton("CounterStrikeDriver") {
            instance<DriverFactory>().createCounterStrikeDriver()
        }
    }
}