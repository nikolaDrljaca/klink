package com.example.domain.model

import java.util.*

data class KlinkKeys(
    val klinkId: UUID,
    val readKey: KlinkKey,
    val writeKey: KlinkKey?
)