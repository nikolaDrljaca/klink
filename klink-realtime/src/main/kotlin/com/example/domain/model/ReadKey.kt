package com.example.domain.model

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

@JvmInline
value class ReadKey private constructor(val value: String) {
    companion object {
        operator fun invoke(value: String): Either<Unit, ReadKey> = either {
            ensure(value.length == 8) { Unit }
            ReadKey(value = value)
        }
    }
}