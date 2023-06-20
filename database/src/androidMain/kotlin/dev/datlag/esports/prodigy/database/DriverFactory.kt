package dev.datlag.esports.prodigy.database

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DriverFactory(
    private val context: Context
) {
    actual fun createHLTVDriver(): SqlDriver {
        return AndroidSqliteDriver(HLTVDB.Schema, context, "hltv.db")
    }
}