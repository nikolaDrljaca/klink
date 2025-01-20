package com.example.route.wssync

import com.example.route.SocketParams
import com.example.route.wssync.manager.CreateSessionResult
import com.example.route.wssync.manager.KlinkWsSyncSessionManager
import com.example.route.wssync.session.SyncSessionEvent
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*

fun Routing.klinkWsSyncSocket() {
    val sessionManager = KlinkWsSyncSessionManager()

    webSocket(SocketParams.WS_SYNC_PATH) {
        // parse session data
        val sessionData = parseWsSyncSessionData()
        // create session
        val session = when (val result = sessionManager.create(sessionData)) {
            CreateSessionResult.InvalidSessionData -> {
                close(CloseReason(CloseReason.Codes.NORMAL, "Invalid request."))
                return@webSocket
            }

            CreateSessionResult.NoAccess -> {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid request."))
                return@webSocket
            }

            is CreateSessionResult.Session -> result.session
        }
        // send changes down to the client
        val valueFlowJob = session.eventFlow()
            .onEach {
                when (it) {
                    SyncSessionEvent.KlinkDeleted -> close(
                        CloseReason(
                            CloseReason.Codes.CANNOT_ACCEPT,
                            "Invalid request."
                        )
                    )

                    is SyncSessionEvent.Payload -> sendSerialized(it.value)
                }
            }
            // launches a new job, not blocking the session handler
            .launchIn(this)

        // process incoming frames -> pass to session
        incoming.receiveAsFlow()
            // runs if the flow is cancelled or completes normally
            .onCompletion {
                valueFlowJob.cancel()
                sessionManager.remove(sessionData)
            }
            .filterIsInstance<Frame.Text>()
            .map { it.readText() }
            // calling collect blocks the socket handler, keeping it open
            .collect { session.process(it) }
    }
}

fun DefaultWebSocketServerSession.parseWsSyncSessionData(): KlinkWsSyncSessionData {
    val klinkId = call.parameters.getOrFail(SocketParams.KLINK_ID)
    val readKey = call.request.queryParameters.getOrFail(SocketParams.READ_KEY)
    val writeKey = call.request.queryParameters[SocketParams.WRITE_KEY]
    // create session data
    return KlinkWsSyncSessionData(
        klinkId = klinkId,
        readKey = readKey,
        writeKey = writeKey,
    )
}
