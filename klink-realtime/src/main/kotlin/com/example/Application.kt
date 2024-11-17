package com.example

import com.example.framework.configureHTTP
import com.example.framework.configureSerialization
import com.example.framework.configureSockets
import com.example.framework.koin.configureKoin
import com.example.route.klinkSockets
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureKoin()

    configureSerialization()
    configureSockets()
    configureHTTP()

    // register routes
    klinkSockets()
}
