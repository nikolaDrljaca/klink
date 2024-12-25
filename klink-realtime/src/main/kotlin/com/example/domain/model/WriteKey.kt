package com.example.domain.model

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

@JvmInline
value class WriteKey private constructor(val value: String) {

    companion object {
        operator fun invoke(value: String): Either<Unit, WriteKey> = either {
            ensure(value.length == 8) { Unit }
            WriteKey(value = value)
        }
    }
}