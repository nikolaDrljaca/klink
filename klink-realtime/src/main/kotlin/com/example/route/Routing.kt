package com.example.route

import com.example.domain.CheckKlinkAccess
import com.example.domain.KlinkValueFlow
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.koin.ktor.ext.inject

fun Application.klinkSockets() {
    val checkKlinkAccess: CheckKlinkAccess by inject()
    val klinkValueFlow: KlinkValueFlow by inject()

    routing {
        healthSocket()

        klinkReadOnlySocket(
            checkKlinkAccess,
            klinkValueFlow
        )
        klinkReadWriteSocket()
    }
}

// ws://realtime:8081/ws/klink/r/{klink_id} -- read
fun Routing.klinkReadOnlySocket(
    checkKlinkAccess: CheckKlinkAccess,
    valueFlow: KlinkValueFlow
) = webSocket("/ws/klink/r/{klink_id}") {
    val klinkId = call.parameters["klink_id"] ?: error("Missing klink id!")
    val readKey = call.request.queryParameters["read_key"] ?: error("Missing read_key!")
    val accessType = CheckKlinkAccess.createAccessType(
        klinkId,
        readKey,
        null
    )
    val hasAccess = checkKlinkAccess.execute(accessType)
    if (!hasAccess) {
        close(CloseReason(CloseReason.Codes.NORMAL, "Not Authorized!"))
    }
    valueFlow.create(klinkId)
        .collect { sendSerialized(it) }
}

// ws://realtime:8081/ws/klink/rw/{klink_id} -- read/write
fun Routing.klinkReadWriteSocket() = webSocket("/ws/klink/rw/{klink_id}") {
    val klinkId = call.parameters["klink_id"]
    val readKey = call.request.queryParameters["read_key"]
    val writeKey = call.request.queryParameters["write_key"]
}

// ws://realtime:8081/ws/health -- health
fun Routing.healthSocket() = webSocket("/ws/health") {
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
