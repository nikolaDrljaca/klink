package com.example.data

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.example.KlinkDatabase
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*

fun provideKlinkDatabase(app: Application): KlinkDatabase {
    val url = app.retrieveDatabaseUrl()
    // extract db host per env
    val hikariConfig = HikariConfig().apply {
        jdbcUrl = url
        driverClassName = "org.postgresql.Driver"
        username = "user"
    }

    return try {
        val dataSource = HikariDataSource(hikariConfig)
        val driver = dataSource.asJdbcDriver()
        KlinkDatabase(driver)
    } catch (e: Exception) {
        error("Unable to initialize SQLDelight database!")
    }
}

private fun Application.retrieveDatabaseUrl(): String {
    val name = "db.url"
    val prop = environment.config.propertyOrNull(name)?.getString()
    if (prop == null) {
        log.info("`$name` property was not set in application environment!")
        error("Application property `$name` not set!")
    }
    return prop
}
