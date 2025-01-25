package com.example.domain.model

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.example.util.isUrl

@JvmInline
value class KlinkEntry private constructor(val url: String) {
    companion object {
        operator fun invoke(value: String) = either {
            ensure(value.isUrl()) { KlinkEntryInvalid }
            KlinkEntry(url = value)
        }

        fun create(value: String): KlinkEntry {
            require(value.isUrl()) { "Cannot explicitly create KlinkEntry with value: '$value'" }
            return KlinkEntry(url = value)
        }
    }
}

data object KlinkEntryInvalid