package com.example.domain

import com.example.data.KlinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import java.util.*

class KlinkSocketEventProcessor(
    private val scope: CoroutineScope,
    private val repo: KlinkRepository,
    private val klinkId: String
) {
    private val eventSink = MutableSharedFlow<KlinkEntryEvent>(
        replay = 0,
        extraBufferCapacity = 0
    )

    suspend fun push(event: KlinkEntryEvent) = coroutineScope {
        eventSink.emit(event)
    }

    private val eventFlow = eventSink
        .flatMapConcat { handleEvent(it) }
        .launchIn(scope)

    private fun handleEvent(event: KlinkEntryEvent) = flow {
        val id = UUID.fromString(klinkId)
        when (event) {
            is KlinkEntryEvent.Add -> repo.insertKlinkEntry(id, event.value)
            is KlinkEntryEvent.Delete -> repo.deleteKlinkEntryByValue(id, event.value)
        }
        // after processing event, emit to continue flow
        emit(Unit)
    }
}