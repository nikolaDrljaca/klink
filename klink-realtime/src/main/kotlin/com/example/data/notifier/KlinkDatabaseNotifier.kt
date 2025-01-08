package com.example.data.notifier

import kotlinx.coroutines.flow.Flow

interface KlinkDatabaseNotifier {

    fun klinkEntityNotifier(): Flow<List<NotifierData>>

}