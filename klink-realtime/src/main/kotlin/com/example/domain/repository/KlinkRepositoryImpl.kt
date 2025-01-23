package com.example.domain.repository

import com.example.KlinkEntryQueries
import com.example.KlinkKeyQueries
import com.example.KlinkQueries
import com.example.domain.model.KlinkEntry
import com.example.domain.model.KlinkKey
import com.example.domain.model.KlinkKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.*

class KlinkRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val klinkDao: KlinkQueries,
    private val klinkEntryDao: KlinkEntryQueries,
    private val keysDao: KlinkKeyQueries
) : KlinkRepository {
    override suspend fun insertKlinkEntry(klinkId: UUID, value: String) = withContext(dispatcher) {
        klinkEntryDao.insertKlinkEntry(
            klinkId,
            value,
            createdAt = LocalDateTime.now()
        )
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
            entries.forEach { entry ->
                klinkEntryDao.insertKlinkEntry(
                    klinkId,
                    entry.url,
                    createdAt = LocalDateTime.now()
                )
            }
        }
    }

    override suspend fun deleteAllEntriesByKlinkId(klinkId: UUID) = withContext(dispatcher) {
        klinkEntryDao.deleteByKlinkId(klinkId)
    }

    override suspend fun replaceEntries(klinkId: UUID, entries: List<KlinkEntry>) = withContext(dispatcher) {
        klinkEntryDao.transaction {
            // current entries
            val current = retrieveEntriesByKlinkId(klinkId).toSet()
            // entries not stored in current -> create them
            entries
                .asSequence()
                .filterNot { current.contains(it) }
                .forEach { entry ->
                    klinkEntryDao.insertKlinkEntry(
                        klinkId,
                        entry.url,
                        createdAt = LocalDateTime.now()
                    )
                }
            // entries in current but not in `entries` -> delete them
            current.asSequence()
                .filterNot { entries.contains(it) }
                .forEach { entry -> klinkEntryDao.deleteKlinkEntryByValue(klinkId, entry.url) }
        }
    }

    override suspend fun findEntriesByKlinkId(klinkId: UUID): List<KlinkEntry> = withContext(dispatcher) {
        retrieveEntriesByKlinkId(klinkId)
    }

    private fun retrieveEntriesByKlinkId(klinkId: UUID): List<KlinkEntry> {
        return klinkEntryDao.findByKlinkId(
            klinkId = klinkId,
            mapper = { _, _, url, createdAt -> KlinkEntry.create(value = url) }
        )
            .executeAsList()
    }
}