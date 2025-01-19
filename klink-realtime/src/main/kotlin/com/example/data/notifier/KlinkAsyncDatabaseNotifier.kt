package com.example.data.notifier

import com.example.LOG
import com.impossibl.postgres.api.jdbc.PGConnection
import com.impossibl.postgres.api.jdbc.PGNotificationListener
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json

class KlinkAsyncDatabaseNotifier(
    private val dataSource: HikariDataSource,
) : KlinkDatabaseNotifier {
    private val connection by lazy { dataSource.connection }
    private val pgConnection by lazy { connection.unwrap(PGConnection::class.java) }
    private val statement by lazy { pgConnection.createStatement() }
    private val json = Json {
        decodeEnumsCaseInsensitive = true
        ignoreUnknownKeys = true
    }

    override fun klinkEntityNotifier(): Flow<KlinkNotifierData> {
        statement.executeUpdate("LISTEN klink_change")

        val notificationFlow = callbackFlow {
            val notificationListener = object : PGNotificationListener {
                override fun notification(
                    processId: Int,
                    channelName: String?,
                    payload: String?
                ) {
                    if (channelName == "klink_change") {
                        payload?.let { trySend(it) }
                    }
                }
            }
            // register listener
            pgConnection.addNotificationListener(notificationListener)

            awaitClose {
                // cleanup
                pgConnection.removeNotificationListener(notificationListener)
            }
        }

        return notificationFlow
            .flowOn(Dispatchers.IO)
            .map { createKlinkNotifierData(it) }
            .filterNotNull()
    }

    override fun klinkEntryEntityNotifier(): Flow<KlinkEntryNotifierData> {
        statement.executeUpdate("LISTEN klink_entry_change")

        val notificationFlow = callbackFlow {
            val notificationListener = object : PGNotificationListener {
                override fun notification(
                    processId: Int,
                    channelName: String?,
                    payload: String?
                ) {
                    if (channelName == "klink_entry_change") {
                        payload?.let { trySend(it) }
                    }
                }
            }
            // register listener
            pgConnection.addNotificationListener(notificationListener)

            awaitClose {
                // cleanup
                pgConnection.removeNotificationListener(notificationListener)
            }
        }

        return notificationFlow
            .flowOn(Dispatchers.IO)
            .map { createKlinkEntryNotifierData(it) }
            .filterNotNull()
    }

    override fun close() {
        LOG.info("Closing async Notifier connection")
        statement.close()
        connection.close()
    }

    private fun createKlinkNotifierData(parameter: String): KlinkNotifierData? {
        return runCatching { json.decodeFromString<KlinkNotifierData>(parameter) }
            .onFailure {
                LOG.warn("Unable to deserialize klink notifier data", it)
            }
            .getOrNull()
    }

    private fun createKlinkEntryNotifierData(parameter: String): KlinkEntryNotifierData? {
        return runCatching { json.decodeFromString<KlinkEntryNotifierData>(parameter) }
            .onFailure {
                LOG.warn("Unable to deserialize klink entry notifier data", it)
            }
            .getOrNull()
    }
}