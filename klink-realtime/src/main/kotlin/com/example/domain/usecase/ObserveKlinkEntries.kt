package com.example.domain.usecase

import com.example.data.notifier.KlinkAsyncDatabaseNotifier
import com.example.domain.model.KlinkEntry
import com.example.domain.repository.KlinkRepository
import kotlinx.coroutines.flow.*
import java.util.*

class ObserveKlinkEntries(
    private val notifier: KlinkAsyncDatabaseNotifier,
    private val repository: KlinkRepository
) {
    fun execute(klinkId: String): Flow<List<KlinkEntry>> = flow {
        // trigger collectors with initial value
        val id = UUID.fromString(klinkId)
        emit(repository.findEntriesByKlinkId(id))
        // collect each notification and fetch
        emitAll(
            notifier.klinkEntryEntityNotifier()
                // take only entries for current klinkId
                .filter { it.row.klinkId == klinkId }
                .map { repository.findEntriesByKlinkId(id) }
        )
    }
}