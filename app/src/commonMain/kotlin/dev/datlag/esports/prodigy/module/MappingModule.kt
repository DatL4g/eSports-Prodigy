package dev.datlag.esports.prodigy.module

import dev.datlag.esports.prodigy.database.AllNews
import dev.datlag.esports.prodigy.model.hltv.Country
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.other.Mapper
import org.kodein.di.DI
import org.kodein.di.bindSingleton

object MappingModule {

    const val NAME = "MappingModule"

    val di = DI.Module(NAME) {
        bindSingleton {
            Mapper.build {
                mapTypes<AllNews, News> {
                    News(
                        link = it.link,
                        title = it.title,
                        date = it.date,
                        country = Country(
                            name = it.name,
                            code = it.countryCode.ifBlank { it.code }
                        )
                    )
                }
            }
        }
    }
}