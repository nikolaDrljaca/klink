package com.example

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Routing.healthRoute() {
    // health endpoint
    get("/health") {
        call.respond(HttpStatusCode.OK, "Service healthy.")
    }
}

// ws://realtime:8081/ws/klink/rw/{klink_id} -- read/write
// ws://realtime:8081/ws/klink/r/{klink_id} -- read
fun Routing.sockets() {
    webSocket("/ws/klink/rw/{klink_id}") {
        val klinkId = call.parameters["klink_id"]
        val readKey = call.request.queryParameters["read_key"]
        val writeKey = call.request.queryParameters["write_key"]
    }

    webSocket("/ws/klink/r/{klink_id}") {
        val klinkId = call.parameters["klink_id"]
        val readKey = call.request.queryParameters["read_key"]
    }
}