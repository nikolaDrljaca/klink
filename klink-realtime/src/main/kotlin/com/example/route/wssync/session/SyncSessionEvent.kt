package com.example.route.wssync.session

import org.openapitools.client.models.SyncPersistenceDataApiDto

sealed interface SyncSessionEvent {
    data class Payload(val value: SyncPersistenceDataApiDto) : SyncSessionEvent

    data object KlinkDeleted : SyncSessionEvent
}