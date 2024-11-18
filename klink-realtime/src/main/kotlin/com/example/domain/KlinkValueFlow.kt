package com.example.domain

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.KlinkEntryQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.openapitools.client.models.KlinkEntryApiDto
import java.util.*

class KlinkValueFlow(
    private val dao: KlinkEntryQueries
) {

    fun create(klinkId: String): Flow<List<KlinkEntryApiDto>> {
        return dao.findByKlinkId(UUID.fromString(klinkId))
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { toApiDto(it) }
    }

    private fun toApiDto(values: List<String>) = values.map { entry -> KlinkEntryApiDto(entry) }
}