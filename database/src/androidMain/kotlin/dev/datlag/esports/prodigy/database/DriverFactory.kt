package dev.datlag.esports.prodigy.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DriverFactory(
    private val context: Context
) {
    actual fun createHLTVDriver(): SqlDriver {
        return AndroidSqliteDriver(HLTVDB.Schema, context, "hltv.db")
    }

    actual fun createCounterStrikeDriver(): SqlDriver {
        return AndroidSqliteDriver(CounterStrikeDB.Schema, context, "cs.db")
    }
}