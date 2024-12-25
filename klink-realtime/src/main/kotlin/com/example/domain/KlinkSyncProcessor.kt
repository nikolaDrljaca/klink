package com.example.domain

import com.example.data.KlinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import java.util.*

class KlinkSyncProcessor(
    private val scope: CoroutineScope,
    private val repo: KlinkRepository,
    private val klinkId: String
) {
    private val jobSink = MutableSharedFlow<Payload>(
        replay = 0,
        extraBufferCapacity = 0
    )

    suspend fun push(payload: Payload) = coroutineScope {
        jobSink.emit(payload)
    }

    private val processorFlow = jobSink
        .flatMapConcat { handleJob(it) }
        .launchIn(scope)

    private fun handleJob(payload: Payload) = flow {
        val id = UUID.fromString(klinkId)
        repo.replaceEntries(id, payload.entries)
        emit(Unit)
    }

    data class Payload(
        val entries: List<String>
    )
}