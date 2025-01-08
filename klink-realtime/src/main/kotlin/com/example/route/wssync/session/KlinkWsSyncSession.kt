package com.example.route.wssync.session

import com.example.LOG
import com.example.data.notifier.KlinkDatabaseNotifier
import com.example.data.notifier.NotifierData
import com.example.domain.KlinkSyncProcessor
import com.example.domain.model.KlinkEntry
import com.example.domain.usecase.ObserveKlinkEntries
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.openapitools.client.models.KlinkEntryApiDto
import org.openapitools.client.models.SyncPersistenceDataApiDto

class KlinkWsSyncSession(
    private val klinkId: String,
    private val isReadOnly: Boolean,
    private val syncProcessor: KlinkSyncProcessor,
    private val observeKlinkEntries: ObserveKlinkEntries,
    private val databaseNotifier: KlinkDatabaseNotifier
) {
    suspend fun process(data: String) {
        if (isReadOnly) {
            return
        }
        val payload = Json.decodeFromString<SyncPersistenceDataApiDto>(data)
        val entries = Json.decodeFromString<List<KlinkEntryApiDto>>(payload.newValue)
        val klinkEntries = entries
            .asSequence()
            .map {
                KlinkEntry(it.value)
                    .onLeft { _ -> LOG.warn("Invalid KlinkEntry passed to sync processor: ${it.value}") }
                    .getOrNull()
            }
            .filterNotNull()
            .toList()
        syncProcessor.push(klinkEntries)
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

    private fun createNotifierFlow() = databaseNotifier.klinkEntityNotifier()
        .map {
            val notifierData = it.find { data -> data.row.id == klinkId }
            notifierData?.let { data ->
                when {
                    data.operation == NotifierData.Operation.DELETED -> true
                    else -> false
                }
            } ?: false
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
