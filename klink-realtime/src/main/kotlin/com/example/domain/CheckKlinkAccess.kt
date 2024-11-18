package com.example.domain

import com.example.data.GetKlinkKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class CheckKlinkAccess(
    private val fetch: GetKlinkKeys,
) {
    suspend fun execute(klinkAccessType: AccessType): Boolean {
        val id = UUID.fromString(klinkAccessType.klinkId)
        val keyEntry = withContext(Dispatchers.IO) { fetch.findByKlinkId(klinkId = id) }
            ?: return false
        return when (klinkAccessType) {
            is AccessType.ReadOnly -> keyEntry.read_key == klinkAccessType.readKey
            is AccessType.ReadWrite -> {
                val readAccess = keyEntry.read_key == klinkAccessType.readKey
                val writeAccess = keyEntry.write_key == klinkAccessType.writeKey
                readAccess and writeAccess
            }
        }
    }

    sealed class AccessType {
        abstract val klinkId: String

        data class ReadOnly(
            val readKey: String,
            override val klinkId: String
        ) : AccessType()

        data class ReadWrite(
            val readKey: String,
            val writeKey: String,
            override val klinkId: String
        ) : AccessType()
    }

    companion object {
        fun createAccessType(
            klinkId: String,
            readKey: String,
            writeKey: String?,
        ): AccessType = when {
            writeKey != null -> AccessType.ReadWrite(readKey, writeKey, klinkId)
            else -> AccessType.ReadOnly(readKey, klinkId)
        }
    }
}