package com.example.route

import com.example.data.KlinkRepository
import com.example.domain.KlinkEntryEvent
import com.example.domain.KlinkSocketEventProcessor
import com.example.domain.usecase.CheckKlinkAccess
import com.example.domain.usecase.ObserveKlinkEntries
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.openapitools.client.models.KlinkEntryApiDto

// TODO: clean up this mess
// ws://realtime:8081/ws/klink/rw/{klink_id} -- read/write
fun Routing.klinkReadWriteSocket(
    checkKlinkAccess: CheckKlinkAccess,
    observeKlinkEntries: ObserveKlinkEntries,
    klinkRepository: KlinkRepository,
    scope: CoroutineScope,
) {
    val sessions = mutableMapOf<String, KlinkSocketEventProcessor>()

    webSocket(SocketParams.WRITE_PATH) {
        val klinkId = call.parameters.getOrFail(SocketParams.KLINK_ID)
        val readKey = call.request.queryParameters.getOrFail(SocketParams.READ_KEY)
        val writeKey = call.request.queryParameters.getOrFail(SocketParams.WRITE_KEY)
        // check access
        val accessType = CheckKlinkAccess.createAccessType(klinkId, readKey, writeKey)
        val hasAccess = checkKlinkAccess.execute(accessType)
        if (!hasAccess) {
            close(CloseReason(CloseReason.Codes.NORMAL, "Not Authorized!"))
        }
        // create processor
        val processor = sessions.getOrPut(klinkId) {
            KlinkSocketEventProcessor(
                scope = scope,
                repo = klinkRepository,
                klinkId = klinkId
            )
        }
        // send changes down to the client
        val valueFlowJob = launch {
            observeKlinkEntries.execute(klinkId)
                .collect { sendSerialized(it) }
        }
        // process socket events -> push events to processor
        try {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val payload = Json.decodeFromString<KlinkWriteSocketPayload>(frame.readText())
                        val event = payload.toEvent()
                        processor.push(event)
                    }

                    is Frame.Close -> sessions.remove(klinkId)
                    else -> Unit
                }
            }
        } catch (ex: ClosedReceiveChannelException) {
            valueFlowJob.cancel()
            sessions.remove(klinkId)
        }
    }
}

@Serializable
enum class KlinkEventType {
    ADD, DELETE
}

@Serializable // to come into the socket
data class KlinkWriteSocketPayload(
    val event: KlinkEventType,
    val data: KlinkEntryApiDto
)

fun KlinkWriteSocketPayload.toEvent(): KlinkEntryEvent {
    return when (event) {
        KlinkEventType.ADD -> KlinkEntryEvent.Add(value = data.value)
        KlinkEventType.DELETE -> KlinkEntryEvent.Delete(value = data.value)
    }
}

