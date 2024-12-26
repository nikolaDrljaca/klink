package com.example.framework

import io.ktor.server.application.*

fun Application.configureHTTP() {
    // NOTE: Enforced by default for security
//    install(CORS) {
//        allowMethod(HttpMethod.Options)
//        allowMethod(HttpMethod.Put)
//        allowMethod(HttpMethod.Delete)
//        allowMethod(HttpMethod.Patch)
//        allowHeader(HttpHeaders.Authorization)
//        anyHost() // fine since app is only accessible from the docker network
//    }
}
