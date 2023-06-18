package dev.datlag.esports.prodigy.module

import dev.datlag.esports.prodigy.network.repository.HLTVRepository
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object NetworkModule {

    const val NAME = "NetworkModule"

    val di = DI.Module(NAME) {
        import(DataStoreModule.di)

        bindSingleton {
            HttpClient(OkHttp) {
                engine {
                    config {
                        followRedirects(true)
                    }
                }
            }
        }

        bindSingleton {
            HLTVRepository(instance())
        }
    }
}