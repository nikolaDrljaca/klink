package com.example.route

import com.example.data.KlinkRepository
import com.example.domain.usecase.CheckKlinkAccess
import com.example.domain.usecase.ObserveKlinkEntries
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import org.koin.ktor.ext.inject

object SocketParams {
    const val KLINK_ID = "klink_id"
    const val READ_KEY = "read_key"
    const val WRITE_KEY = "write_key"

    const val READ_PATH = "/ws/klink/r/{$KLINK_ID}"
    const val WRITE_PATH = "/ws/klink/rw/{$KLINK_ID}"
}

fun Application.klinkSockets() {
    val checkKlinkAccess: CheckKlinkAccess by inject()
    val observeKlinkEntries: ObserveKlinkEntries by inject()
    val klinkRepository: KlinkRepository by inject()
    val scope: CoroutineScope by inject()

    routing {
        healthSocket()

        klinkReadOnlySocket(
            checkKlinkAccess,
            observeKlinkEntries,
        )

        klinkReadWriteSocket(
            checkKlinkAccess,
            observeKlinkEntries,
            klinkRepository,
            scope
        )
    }
}

// ws://realtime:8081/ws/klink/r/{klink_id} -- read
fun Routing.klinkReadOnlySocket(
    checkKlinkAccess: CheckKlinkAccess,
    valueFlow: ObserveKlinkEntries
) = webSocket(SocketParams.READ_PATH) {
    val klinkId = call.parameters.getOrFail(SocketParams.KLINK_ID)
    val readKey = call.request.queryParameters.getOrFail(SocketParams.READ_KEY)
    // TODO: use case composition - combine access check and valueFlow into one use case
    val accessType = CheckKlinkAccess.createAccessType(
        klinkId,
        readKey,
        null
    )
    val hasAccess = checkKlinkAccess.execute(accessType)
    if (!hasAccess) {
        close(CloseReason(CloseReason.Codes.NORMAL, "Not Authorized!"))
    }
    valueFlow.execute(klinkId)
        .collect { sendSerialized(it) }
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
