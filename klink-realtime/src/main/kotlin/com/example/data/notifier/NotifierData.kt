package com.example.data.notifier

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotifierData(
    val operation: Operation,
    val row: Row
) {
    @Serializable
    enum class Operation(val value: String) {
        DELETED("deleted"),
        INSERTED("inserted"),
        UPDATED("updated"),
    }

    @Serializable
    data class Row(
        val id: String,
        val name: String,
        val description: String?,
        @SerialName("created_at") val createdAt: String,
        @SerialName("modified_at") val modifiedAt: String
    )
}