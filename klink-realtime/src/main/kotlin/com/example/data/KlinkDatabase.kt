package com.example.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.example.KlinkDatabase
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*

fun provideHikariDataSource(app: Application): HikariDataSource {
    val url = app.retrieveDatabaseUrl()
    val hikariConfig = HikariConfig().apply {
        jdbcUrl = url
        driverClassName = "org.postgresql.Driver"
        username = "user"
    }
    return HikariDataSource(hikariConfig)
}

fun provideKlinkDatabase(
    hikariDataSource: HikariDataSource
): KlinkDatabase =
    try {
        val driver: SqlDriver = hikariDataSource.asJdbcDriver()
        // wrap in NotifyDriver so klink entry manipulation notifies observers properly
        val notifyDriver = JdbcNotifyDriver(driver)
        KlinkDatabase(notifyDriver)
    } catch (e: Exception) {
        error("Unable to initialize SQLDelight database!")
    }

private fun Application.retrieveDatabaseUrl(): String {
    // extract db host per env
    val name = "db.url"
    val prop = environment.config.propertyOrNull(name)?.getString()
    if (prop == null) {
        log.info("`$name` property was not set in application environment!")
        error("Application property `$name` not set!")
    }
    return prop
}
