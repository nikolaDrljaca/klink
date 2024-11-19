package com.example.domain.usecase

import com.example.Klink_key
import java.util.*

fun interface GetKlinkKeys {
    suspend fun findByKlinkId(klinkId: UUID): Klink_key?
}