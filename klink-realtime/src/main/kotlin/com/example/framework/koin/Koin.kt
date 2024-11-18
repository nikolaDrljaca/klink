package com.example.framework.koin

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    val modules = appModules(this)
    install(Koin) {
        slf4jLogger()
        modules(modules)
    }
}