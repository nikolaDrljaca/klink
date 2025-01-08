package com.example.domain.usecase

import com.example.domain.model.KlinkAccessType
import com.example.domain.model.KlinkKeys
import com.example.domain.repository.KlinkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CheckKlinkAccess(
    private val dispatcher: CoroutineDispatcher,
    private val repo: KlinkRepository
) {
    suspend fun execute(probe: KlinkKeys): KlinkAccessType {
        val keyEntry = withContext(dispatcher) { repo.findKeysByKlinkId(probe.klinkId) }
        if (keyEntry == null) return KlinkAccessType.NO_ACCESS
        val hasFullAccess = with(probe) {
            val read = (readKey.value == keyEntry.readKey.value)
            val write = keyEntry.writeKey?.let {
                when {
                    writeKey != null -> writeKey.value == it.value
                    else -> false
                }
            } ?: false
            read and write
        }
        val hasReadAccess = probe.readKey.value == keyEntry.readKey.value

        return when {
            hasFullAccess -> KlinkAccessType.FULL_ACCESS
            hasReadAccess -> KlinkAccessType.READ_ACCESS
            else -> KlinkAccessType.NO_ACCESS
        }
    }
}

