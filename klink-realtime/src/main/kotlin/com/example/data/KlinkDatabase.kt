package com.example.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.example.KlinkDatabase
import com.impossibl.postgres.jdbc.PGDataSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*

fun provideHikariDataSource(app: Application): HikariDataSource {
    val url = app.retrieveDatabaseUrl()
    val hikariConfig = HikariConfig().apply {
        dataSource = PGDataSource().apply {
            user = "user"
            this.url = url
        }
    }
    return HikariDataSource(hikariConfig)
}

fun provideKlinkDatabase(
    dataSource: HikariDataSource,
): KlinkDatabase =
    try {
        val driver: SqlDriver = dataSource.asJdbcDriver()
        // wrap in NotifyDriver so klink entry manipulation notifies observers properly
        //val notifyDriver = JdbcNotifyDriver(driver)
        KlinkDatabase(driver)
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
