package com.example.domain

import com.example.LOG
import com.example.domain.model.KlinkEntry
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import org.openapitools.client.models.KlinkEntryApiDto
import org.openapitools.client.models.SyncPersistenceDataApiDto
import java.util.*

typealias KlinkWsSyncDataProcessor = suspend (klinkId: UUID, data: List<KlinkEntry>) -> Unit

suspend fun writeKlinkWsSyncData(
    klinkId: UUID,
    data: String,
    processor: KlinkWsSyncDataProcessor
) = coroutineScope {
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
    processor(klinkId, klinkEntries)
}
