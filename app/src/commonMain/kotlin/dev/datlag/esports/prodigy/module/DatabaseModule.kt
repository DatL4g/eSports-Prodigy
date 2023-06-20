package dev.datlag.esports.prodigy.module

import dev.datlag.esports.prodigy.database.HLTVDB
import dev.datlag.esports.prodigy.model.hltv.Country
import dev.datlag.esports.prodigy.model.hltv.News
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object DatabaseModule {

    const val NAME = "DatabaseModule"

    val di = DI.Module(NAME) {
        import(DataStoreModule.di)

        bindSingleton {
            HLTVDB(instance("HLTVDriver"))
        }

        bindSingleton("HLTVNewsList") {
            val hltvDB: HLTVDB = instance()
            hltvDB.hLTVQueries.allNews { link, title, date, _, name, code ->
                News(
                    link = link,
                    title = title,
                    date = date,
                    country = Country(
                        name = name,
                        code = code
                    )
                )
            }.executeAsList()
        }
    }
}