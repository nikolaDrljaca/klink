package com.example.domain.model

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.util.isUuid
import java.util.*

data class KlinkAccessProbe(
    val klinkId: UUID,
    val readKey: ReadKey,
    val writeKey: WriteKey?,
) {
    companion object {
        fun create(
            klinkId: String,
            readKey: String,
            writeKey: String?
        ): Either<Unit, KlinkAccessProbe> {
            if (writeKey == null) {
                return either {
                    ensure(klinkId.isUuid()) { Unit }
                    KlinkAccessProbe(
                        UUID.fromString(klinkId),
                        ReadKey(readKey).bind(),
                        null
                    )
                }
            }
            return either {
                ensure(klinkId.isUuid()) { }
                KlinkAccessProbe(
                    UUID.fromString(klinkId),
                    ReadKey(readKey).bind(),
                    WriteKey(writeKey).bind(),
                )
            }
        }
    }
}