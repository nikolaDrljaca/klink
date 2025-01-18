package com.example.data.notifier

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Operation(val value: String) {
    DELETED("deleted"),
    INSERTED("inserted"),
    UPDATED("updated"),
}

@Serializable
data class KlinkNotifierData(
    val operation: Operation,
    val row: Row
) {
    @Serializable
    data class Row(
        val id: String,
        val name: String,
        val description: String?,
        @SerialName("created_at") val createdAt: String,
        @SerialName("modified_at") val modifiedAt: String
    )
}

@Serializable
data class KlinkEntryNotifierData(
    val operation: Operation,
    val row: Row
) {
    @Serializable
    data class Row(
        @SerialName("id") val id: String,
        @SerialName("klink_id") val klinkId: String,
        @SerialName("value") val value: String
    )
}