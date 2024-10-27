package com.example

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.example.plugins.configureHTTP
import com.example.plugins.configureSerialization
import com.example.plugins.configureSockets
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureHTTP()
    configureDatabase()

    // register routes
    klinkRoutes()
    klinkSockets()
}

fun Application.klinkRoutes() = routing {
    healthRoute()
}
fun Application.klinkSockets() = routing {
    sockets()
}

fun Application.configureDatabase() {
    // extract db host per env
    val url = environment.config.propertyOrNull("db.url")?.getString() ?: error("")
    val hikariConfig = HikariConfig()
    // See https://jdbc.postgresql.org/documentation/use/
    hikariConfig.jdbcUrl = url
    hikariConfig.driverClassName = "org.postgresql.Driver"
    hikariConfig.username = "user"
//    hikariConfig.password = "dbpassword"
    val dataSource = HikariDataSource(hikariConfig)
    val driver = dataSource.asJdbcDriver()
    // driver can be used to configure database
}
