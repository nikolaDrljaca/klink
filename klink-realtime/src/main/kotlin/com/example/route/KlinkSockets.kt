package com.example.route

import com.example.route.wssync.klinkWsSyncSocket
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

object SocketParams {
    const val KLINK_ID = "klink_id"
    const val READ_KEY = "read_key"
    const val WRITE_KEY = "write_key"

    const val WS_SYNC_PATH = "/ws/klink/wsSync/{$KLINK_ID}"
}

fun Application.klinkSockets() {
    routing {
        healthSocket()
        klinkWsSyncSocket()
    }
}

// ws://realtime:8081/ws/health -- health
fun Routing.healthSocket() {
    val healthy = mapOf(
        "status" to "healthy"
    )
    webSocket("/ws/health") {
        sendSerialized(healthy)
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    sendSerialized(healthy)
                }

                else -> continue
            }
        }
    }
}

