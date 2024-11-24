package com.example.util

import java.util.*

fun String.isUuid(): Boolean =
    kotlin.runCatching { UUID.fromString(this) }.isSuccess
