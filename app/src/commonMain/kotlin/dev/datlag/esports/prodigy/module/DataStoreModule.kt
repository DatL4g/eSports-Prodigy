package dev.datlag.esports.prodigy.module

import androidx.datastore.core.DataStoreFactory
import dev.datlag.esports.prodigy.database.HLTVDB
import dev.datlag.esports.prodigy.datastore.AppSettingsSerializer
import dev.datlag.esports.prodigy.datastore.UserSettingsSerializer
import dev.datlag.esports.prodigy.model.ThemeMode
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object DataStoreModule {

    private const val NAME = "DataStoreModule"

    val di = DI.Module(NAME) {
        import(PlatformModule.di)

        bindSingleton {
            DataStoreFactory.create(
                UserSettingsSerializer(instance("UserSettingsCrypto")),
                produceFile = { instance("UserSettingsFile") }
            )
        }
        bindSingleton {
            DataStoreFactory.create(
                AppSettingsSerializer(
                    defaultThemeMode = ThemeMode.SYSTEM.saveValue
                ),
                produceFile = { instance("AppSettingsFile") }
            )
        }
    }
}