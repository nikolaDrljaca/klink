package com.example

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

// ws://realtime:8081/ws/klink/rw/{klink_id} -- read/write
// ws://realtime:8081/ws/klink/r/{klink_id} -- read
// ws://realtime:8081/ws/health -- health
fun Routing.sockets() {

    webSocket("/ws/health") {
        val healthy = mapOf(
            "status" to "healthy"
        )
        sendSerialized(healthy)
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val text = frame.readText()
                    sendSerialized(healthy)
                }
                else -> continue
            }
        }
    }

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