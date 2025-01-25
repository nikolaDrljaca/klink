package com.example.route.wssync.session

import com.example.data.notifier.KlinkDatabaseNotifier
import com.example.data.notifier.Operation
import com.example.domain.repository.KlinkRepository
import com.example.domain.usecase.ObserveKlinkEntries
import com.example.domain.writeKlinkWsSyncData
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.openapitools.client.models.KlinkEntryApiDto
import org.openapitools.client.models.SyncPersistenceDataApiDto
import java.util.*

class KlinkWsSyncSession(
    private val klinkId: String,
    private val isReadOnly: Boolean,
    private val klinkRepository: KlinkRepository,
    private val observeKlinkEntries: ObserveKlinkEntries,
    private val databaseNotifier: KlinkDatabaseNotifier
) {
    suspend fun process(data: String) {
        if (isReadOnly) {
            return
        }
        writeKlinkWsSyncData(
            klinkId = UUID.fromString(klinkId),
            data = data,
            processor = klinkRepository::replaceEntries
        )
    }

    fun eventFlow() = combine(
        createSessionPayloadFlow(),
        createNotifierFlow()
    ) { entryEvent, deletedEvent ->
        when {
            deletedEvent -> SyncSessionEvent.KlinkDeleted
            else -> entryEvent
        }
    }

    private fun createNotifierFlow() = flow {
        // trigger combine with initial value
        emit(false)
        // emit all values from notifier
        emitAll(
            databaseNotifier.klinkEntityNotifier()
                .filter { it.row.id == klinkId }
                .map {
                    when {
                        it.operation == Operation.DELETED -> true
                        else -> false
                    }
                }
        )
    }

    // TODO: Mapping to ApiDtos should be done in routing layer
    private fun createSessionPayloadFlow() = observeKlinkEntries.execute(klinkId)
        .map {
            it.map { entry -> KlinkEntryApiDto(entry.url) }
        }
        .map {
            val data = SyncPersistenceDataApiDto(
                key = klinkId,
                newValue = Json.encodeToString(it),
                timeStamp = System.currentTimeMillis(),
                url = ""
            )
            SyncSessionEvent.Payload(data)
        }
}
