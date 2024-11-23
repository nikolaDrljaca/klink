package com.example.data

import com.example.KlinkEntryQueries
import com.example.KlinkKeyQueries
import com.example.KlinkQueries
import com.example.Klink_key
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*

class KlinkRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val klinkDao: KlinkQueries,
    private val klinkEntryDao: KlinkEntryQueries,
    private val keysDao: KlinkKeyQueries
) : KlinkRepository {
    override suspend fun insertKlinkEntry(klinkId: UUID, value: String) = withContext(dispatcher) {
        klinkEntryDao.insertKlinkEntry(klinkId, value)
    }

    override suspend fun deleteKlinkEntryByValue(klinkId: UUID, value: String) = withContext(dispatcher) {
        klinkEntryDao.deleteKlinkEntryByValue(klinkId, value)
    }

    override suspend fun findKeysByKlinkId(klinkId: UUID): Klink_key? = withContext(dispatcher) {
        keysDao.findByKlinkId(klinkId)
            .executeAsOneOrNull()
    }

    override suspend fun insertAll(klinkId: UUID, entries: List<String>) = withContext(dispatcher) {
        klinkEntryDao.transaction {
            entries.forEach { entry -> klinkEntryDao.insertKlinkEntry(klinkId, entry) }
        }
    }

    override suspend fun deleteAllEntriesByKlinkId(klinkId: UUID) = withContext(dispatcher) {
        klinkEntryDao.deleteByKlinkId(klinkId)
    }

    override suspend fun replaceEntries(klinkId: UUID, entries: List<String>) = withContext(dispatcher) {
        klinkEntryDao.transaction {
            // delete existing entries
            klinkEntryDao.deleteByKlinkId(klinkId)
            // insert new ones
            entries.forEach { entry -> klinkEntryDao.insertKlinkEntry(klinkId, entry) }
        }
    }
}