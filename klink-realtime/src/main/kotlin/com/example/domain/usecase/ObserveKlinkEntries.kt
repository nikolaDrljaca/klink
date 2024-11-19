package com.example.domain.usecase

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.KlinkEntryQueries
import com.example.Klink_entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.openapitools.client.models.KlinkEntryApiDto
import java.util.*

class ObserveKlinkEntries(
    private val dao: KlinkEntryQueries
) {

    fun execute(klinkId: String): Flow<List<KlinkEntryApiDto>> {
        return dao.findByKlinkId(UUID.fromString(klinkId))
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { toApiDto(it) }
    }

    private fun toApiDto(values: List<Klink_entry>) = values.map { entry -> KlinkEntryApiDto(entry.value_) }
}