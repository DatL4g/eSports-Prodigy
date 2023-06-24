package dev.datlag.esports.prodigy.module

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfitBuilder
import dev.datlag.esports.prodigy.network.Steam
import dev.datlag.esports.prodigy.network.converter.FlowerResponseConverter
import dev.datlag.esports.prodigy.network.repository.CSStatsRepository
import dev.datlag.esports.prodigy.network.repository.HLTVRepository
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object NetworkModule {

    const val NAME = "NetworkModule"

    val di = DI.Module(NAME) {
        import(DatabaseModule.di)

        bindSingleton {
            HttpClient(OkHttp) {
                engine {
                    config {
                        followRedirects(true)
                    }
                }
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
            }
        }

        bindSingleton {
            // ToDo("save home data")
            HLTVRepository(instance(), null, instance("HLTVNewsList"))
        }

        bindSingleton {
            ktorfitBuilder {
                converterFactories(FlowerResponseConverter())
                httpClient(instance<HttpClient>())
            }
        }
        bindSingleton("SteamAPI") {
            val builder: Ktorfit.Builder = instance()
            builder.build {
                baseUrl("https://api.steampowered.com/")
            }
        }
        bindSingleton {
            val steamApi: Ktorfit = instance("SteamAPI")
            steamApi.create<Steam>()
        }
        bindSingleton {
            CSStatsRepository(instance())
        }
    }
}