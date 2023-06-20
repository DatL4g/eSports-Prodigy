package dev.datlag.esports.prodigy.database

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.io.File

actual class DriverFactory(
    private val hltvFile: File
) {
    actual fun createHLTVDriver(): SqlDriver {
        val driver = JdbcSqliteDriver("jdbc:sqlite:${hltvFile.absolutePath}")
        HLTVDB.Schema.create(driver)
        return driver
    }
}