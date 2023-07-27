package dev.datlag.esports.prodigy.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DriverFactory(
    private val hltvFile: File,
    private val counterStrikeFile: File
) {
    actual fun createHLTVDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:${hltvFile.absolutePath}")
        HLTVDB.Schema.create(driver)
        return driver
    }

    actual fun createCounterStrikeDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:${counterStrikeFile.absolutePath}")
        CounterStrikeDB.Schema.create(driver)
        return driver
    }
}