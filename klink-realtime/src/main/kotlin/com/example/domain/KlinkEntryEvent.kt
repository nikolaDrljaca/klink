package com.example.domain

sealed interface KlinkEntryEvent {
    data class Add(val value: String) : KlinkEntryEvent
    data class Delete(val value: String) : KlinkEntryEvent
}