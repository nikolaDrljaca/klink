package com.example.domain.usecase

import com.example.domain.model.KlinkAccessProbe
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RunKlinkAccessProbe(
    private val dispatcher: CoroutineDispatcher,
    private val getKeys: GetKlinkKeys
) {
    suspend fun execute(probe: KlinkAccessProbe): Result {
        val keyEntry = withContext(dispatcher) { getKeys.findByKlinkId(probe.klinkId) }
        if (keyEntry == null) return Result.NO_ACCESS
        val hasFullAccess = with(probe) {
            val read = (readKey.value == keyEntry.read_key)
            val write = when {
                writeKey != null -> writeKey.value == keyEntry.write_key
                else -> false
            }
            read and write
        }
        val hasReadAccess = probe.readKey.value == keyEntry.read_key
        return when {
            hasFullAccess -> Result.FULL_ACCESS
            hasReadAccess -> Result.READ_ACCESS
            else -> Result.NO_ACCESS
        }
    }

    enum class Result {
        NO_ACCESS,
        READ_ACCESS,
        FULL_ACCESS
    }
}