package com.example.domain.repository

import com.example.domain.model.KlinkEntry
import com.example.domain.model.KlinkKeys
import java.util.*

interface KlinkRepository {

    suspend fun insertKlinkEntry(klinkId: UUID, value: String)

    suspend fun deleteKlinkEntryByValue(klinkId: UUID, value: String)

    suspend fun findKeysByKlinkId(klinkId: UUID): KlinkKeys?

    suspend fun insertAll(klinkId: UUID, entries: List<KlinkEntry>)

    suspend fun deleteAllEntriesByKlinkId(klinkId: UUID)

    suspend fun replaceEntries(klinkId: UUID, entries: List<KlinkEntry>)

    suspend fun findEntriesByKlinkId(klinkId: UUID): List<KlinkEntry>
}