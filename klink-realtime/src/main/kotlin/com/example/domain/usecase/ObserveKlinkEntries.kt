package com.example.domain.usecase

import com.example.domain.model.KlinkEntry
import com.example.domain.repository.KlinkRepository
import kotlinx.coroutines.flow.Flow
import java.util.*

/*
NOTE: Additional layer present here since the implementation might be replaced with a `Notifier` type abstraction
if we switch to Exposed and pgjdbc-ng
 */
class ObserveKlinkEntries(
    private val repository: KlinkRepository
) {
    fun execute(klinkId: String): Flow<List<KlinkEntry>> {
        return repository.findEntriesByKlinkId(UUID.fromString(klinkId))
    }
}