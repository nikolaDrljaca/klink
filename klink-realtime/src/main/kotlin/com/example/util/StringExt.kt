package com.example.util

import io.ktor.http.*
import java.util.*

fun String.isUuid(): Boolean =
    kotlin.runCatching { UUID.fromString(this) }.isSuccess

fun String.isUrl(): Boolean = parseUrl(this) != null
