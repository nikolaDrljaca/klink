package com.example

import com.example.data.notifier.KlinkDatabaseNotifier
import com.example.framework.configureHTTP
import com.example.framework.configureSerialization
import com.example.framework.configureSockets
import com.example.framework.koin.configureKoin
import com.example.route.klinkSockets
import io.ktor.server.application.*
import io.ktor.util.logging.*
import org.koin.ktor.ext.get

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

    // cleanup ?
    monitor.subscribe(ApplicationStopped) {
        val notifier: KlinkDatabaseNotifier = get()
        notifier.close()
    }
}

val LOG = KtorSimpleLogger("com.example.Realtime")
