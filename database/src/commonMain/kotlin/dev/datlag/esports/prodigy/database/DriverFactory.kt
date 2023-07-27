package dev.datlag.esports.prodigy.database

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createHLTVDriver(): SqlDriver

    fun createCounterStrikeDriver(): SqlDriver
}