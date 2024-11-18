package com.example.route

import com.example.domain.CheckKlinkAccess
import com.example.domain.KlinkValueFlow
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.koin.ktor.ext.inject

private object Params {
    const val KLINK_ID = "klink_id"
    const val READ_KEY = "read_key"
    const val WRITE_KEY = "write_key"

    const val READ_PATH = "/ws/klink/r/{$KLINK_ID}"
    const val WRITE_PATH = "/ws/klink/rw/{$KLINK_ID}"
}

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
) = webSocket(Params.READ_PATH) {
    val klinkId = call.parameters.getOrFail(Params.KLINK_ID)
    val readKey = call.request.queryParameters.getOrFail(Params.READ_KEY)
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
fun Routing.klinkReadWriteSocket() = webSocket(Params.WRITE_PATH) {
    val klinkId = call.parameters.getOrFail(Params.KLINK_ID)
    val readKey = call.request.queryParameters.getOrFail(Params.READ_KEY)
    val writeKey = call.request.queryParameters.getOrFail(Params.WRITE_KEY)
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
