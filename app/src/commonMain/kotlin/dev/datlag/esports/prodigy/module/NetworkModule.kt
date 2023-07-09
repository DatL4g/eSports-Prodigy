package dev.datlag.esports.prodigy.module

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfitBuilder
import dev.datlag.esports.prodigy.network.Steam
import dev.datlag.esports.prodigy.network.SteamAPI
import dev.datlag.esports.prodigy.network.converter.FlowerResponseConverter
import dev.datlag.esports.prodigy.network.repository.CSStatsRepository
import dev.datlag.esports.prodigy.network.repository.HLTVRepository
import dev.datlag.esports.prodigy.network.repository.SteamRepository
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.xml.*
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object NetworkModule {

    const val NAME = "NetworkModule"

    val di = DI.Module(NAME) {
        import(DatabaseModule.di)

        bindSingleton {
            val xml = XML {
                recommended()
                defaultPolicy {
                    ignoreUnknownChildren()
                }
            }

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
                    xml(xml, ContentType.Text.Xml)
                    xml(xml, ContentType.Application.Xml)
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
        bindSingleton("Steam") {
            val builder: Ktorfit.Builder = instance()
            builder.build {
                baseUrl("https://steamcommunity.com/")
            }
        }
        bindSingleton {
            val steamApi: Ktorfit = instance("SteamAPI")
            steamApi.create<SteamAPI>()
        }
        bindSingleton {
            val steam: Ktorfit = instance("Steam")
            steam.create<Steam>()
        }
        bindSingleton {
            CSStatsRepository(instance())
        }
        bindSingleton {
            SteamRepository(instance())
        }
    }
}