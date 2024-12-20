package com.example.route

import com.example.data.KlinkRepository
import com.example.domain.KlinkSyncProcessor
import com.example.domain.model.KlinkAccessProbe
import com.example.domain.usecase.ObserveKlinkEntries
import com.example.domain.usecase.RunKlinkAccessProbe
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.openapitools.client.models.KlinkEntryApiDto
import org.openapitools.client.models.SyncPersistenceDataApiDto


fun Routing.klinkWsSyncSocket(
    runAccessProbe: RunKlinkAccessProbe,
    observeKlinkEntries: ObserveKlinkEntries,
    klinkRepository: KlinkRepository,
    scope: CoroutineScope,
) {
    val sessions = mutableMapOf<String, KlinkSyncProcessor>()

    webSocket(SocketParams.WS_SYNC_PATH) {
        val klinkId = call.parameters.getOrFail(SocketParams.KLINK_ID)
        val readKey = call.request.queryParameters.getOrFail(SocketParams.READ_KEY)
        val writeKey = call.request.queryParameters[SocketParams.WRITE_KEY]
        val accessProbe = KlinkAccessProbe.create(
            klinkId = klinkId,
            readKey = readKey,
            writeKey = writeKey,
        )
        if (accessProbe.isLeft()) {
            close(CloseReason(CloseReason.Codes.NORMAL, "Invalid request."))
        }
        val accessResult = runAccessProbe.execute(accessProbe.getOrNull()!!)
        // no keys were found, close socket
        if (accessResult === RunKlinkAccessProbe.Result.NO_ACCESS) {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid request."))
        }
        // create processor
        val processor = sessions.getOrPut(klinkId) {
            KlinkSyncProcessor(
                scope = scope,
                repo = klinkRepository,
                klinkId = klinkId
            )
        }
        // send changes down to the client
        val valueFlowJob = launch {
            observeKlinkEntries.execute(klinkId)
                // TODO map and serialize
                .map {
                    SyncPersistenceDataApiDto(
                        key = klinkId,
                        newValue = Json.encodeToString(it),
                        timeStamp = System.currentTimeMillis(),
                        url = ""
                    )
                }
                .collect { sendSerialized(it) }
        }
        // process socket events -> push events to processor
        // TODO: clean up this mess -> incoming.consumeAsFlow maybe?
        try {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        if (accessResult == RunKlinkAccessProbe.Result.FULL_ACCESS) {
                            // TODO map and deserialize
                            val payload = Json.decodeFromString<SyncPersistenceDataApiDto>(frame.readText())
                            val event = payload.toPayload()
                            processor.push(event)
                        }
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

// TODO will need to create domain type and that type should validate -> Payload should contain KlinkEntry type
fun SyncPersistenceDataApiDto.toPayload(): KlinkSyncProcessor.Payload {
    //newValue - list of KlinkEntryApiDto
    val dtos = Json.decodeFromString<List<KlinkEntryApiDto>>(newValue)
    return KlinkSyncProcessor.Payload(
        entries = dtos.map { it.value }
    )
}
