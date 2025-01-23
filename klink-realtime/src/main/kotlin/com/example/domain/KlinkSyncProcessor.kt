package com.example.domain

import com.example.domain.model.KlinkEntry
import com.example.domain.repository.KlinkRepository
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
    private val jobSink = MutableSharedFlow<List<KlinkEntry>>(
        replay = 0,
        extraBufferCapacity = 0
    )

    suspend fun push(payload: List<KlinkEntry>) = coroutineScope {
        jobSink.emit(payload)
    }

    private val processorFlow = jobSink
        .flatMapConcat { handleJob(it) }
        .launchIn(scope)

    private fun handleJob(payload: List<KlinkEntry>) = flow {
        val id = UUID.fromString(klinkId)
        repo.replaceEntries(id, payload)
        emit(Unit)
    }
}