package com.example.domain.model

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

@JvmInline
value class KlinkKey private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<KlinkKeyInvalidLength, KlinkKey> = either {
            ensure(value.length == 8) { KlinkKeyInvalidLength }
            KlinkKey(value = value)
        }

        fun create(value: String): KlinkKey {
            require(value.length == 8) { "Invalid length for KlinkKey!" }
            return KlinkKey(value = value)
        }
    }
}

data object KlinkKeyInvalidLength