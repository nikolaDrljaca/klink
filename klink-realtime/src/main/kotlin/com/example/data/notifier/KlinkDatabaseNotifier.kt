package com.example.data.notifier

import kotlinx.coroutines.flow.Flow

interface KlinkDatabaseNotifier : AutoCloseable {

    fun klinkEntityNotifier(): Flow<KlinkNotifierData>

    fun klinkEntryEntityNotifier(): Flow<KlinkEntryNotifierData>

}