package com.example.domain.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.KlinkEntryQueries
import com.example.KlinkKeyQueries
import com.example.KlinkQueries
import com.example.domain.model.KlinkEntry
import com.example.domain.model.KlinkKey
import com.example.domain.model.KlinkKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
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

    override suspend fun findKeysByKlinkId(klinkId: UUID): KlinkKeys? = withContext(dispatcher) {
        val keys = keysDao.findByKlinkId(klinkId)
            .executeAsOneOrNull()
        keys?.let {
            KlinkKeys(
                klinkId = klinkId,
                readKey = KlinkKey.create(it.read_key),
                writeKey = KlinkKey.create(it.write_key)
            )
        }
    }

    override suspend fun insertAll(klinkId: UUID, entries: List<KlinkEntry>) = withContext(dispatcher) {
        klinkEntryDao.transaction {
            entries.forEach { entry -> klinkEntryDao.insertKlinkEntry(klinkId, entry.url) }
        }
    }

    override suspend fun deleteAllEntriesByKlinkId(klinkId: UUID) = withContext(dispatcher) {
        klinkEntryDao.deleteByKlinkId(klinkId)
    }

    override suspend fun replaceEntries(klinkId: UUID, entries: List<KlinkEntry>) = withContext(dispatcher) {
        klinkEntryDao.transaction {
            // delete existing entries
            klinkEntryDao.deleteByKlinkId(klinkId)
            // insert new ones
            entries.forEach { entry -> klinkEntryDao.insertKlinkEntry(klinkId, entry.url) }
        }
    }

    override fun findEntriesByKlinkId(klinkId: UUID): Flow<List<KlinkEntry>> {
        return klinkEntryDao.findByKlinkId(
            klinkId = klinkId,
            mapper = { _, _, url -> KlinkEntry.create(value = url) }
        )
            .asFlow()
            .mapToList(dispatcher)
            .flowOn(dispatcher)
    }
}