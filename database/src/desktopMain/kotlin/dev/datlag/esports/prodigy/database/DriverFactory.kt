package dev.datlag.esports.prodigy.database

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
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