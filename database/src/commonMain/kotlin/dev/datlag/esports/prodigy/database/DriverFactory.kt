package dev.datlag.esports.prodigy.database

import com.squareup.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createHLTVDriver(): SqlDriver

    fun createCounterStrikeDriver(): SqlDriver
}