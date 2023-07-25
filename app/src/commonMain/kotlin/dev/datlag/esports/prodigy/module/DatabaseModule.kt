package dev.datlag.esports.prodigy.module

import dev.datlag.esports.prodigy.database.AllNews
import dev.datlag.esports.prodigy.database.HLTVDB
import dev.datlag.esports.prodigy.database.CounterStrikeDB
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.other.Mapper
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object DatabaseModule {

    const val NAME = "DatabaseModule"

    val di = DI.Module(NAME) {
        import(DataStoreModule.di)
        import(MappingModule.di)

        bindSingleton {
            HLTVDB(instance("HLTVDriver"))
        }

        bindSingleton {
            CounterStrikeDB(instance("CounterStrikeDriver"))
        }

        bindSingleton("HLTVNewsList") {
            val hltvDB: HLTVDB = instance()
            val mapper: Mapper = instance()

            mapper.mapCollection<AllNews, News>(hltvDB.hLTVQueries.allNews().executeAsList())
        }
    }
}