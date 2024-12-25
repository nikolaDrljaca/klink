package com.example.data

import com.example.Klink_key
import java.util.*

interface KlinkRepository {

    suspend fun insertKlinkEntry(klinkId: UUID, value: String)

    suspend fun deleteKlinkEntryByValue(klinkId: UUID, value: String)

    suspend fun findKeysByKlinkId(klinkId: UUID): Klink_key?

    suspend fun insertAll(klinkId: UUID, entries: List<String>)

    suspend fun deleteAllEntriesByKlinkId(klinkId: UUID)

    suspend fun replaceEntries(klinkId: UUID, entries: List<String>)
}