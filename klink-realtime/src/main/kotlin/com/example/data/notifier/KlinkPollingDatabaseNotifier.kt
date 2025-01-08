package com.example.data.notifier

import com.example.LOG
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import kotlinx.serialization.json.Json
import org.postgresql.PGConnection
import kotlin.time.Duration.Companion.seconds

class KlinkPollingDatabaseNotifier(
    private val dispatcher: CoroutineDispatcher,
    private val dataSource: HikariDataSource
) : KlinkDatabaseNotifier {

    override fun klinkEntityNotifier(): Flow<List<NotifierData>> {
        return observe(Table.KLINK)
            .flowOn(dispatcher)
    }

    private fun observe(table: Table) = callbackFlow<List<NotifierData>> {
        val connection = dataSource.connection
        val pgConnection = connection.unwrap(PGConnection::class.java)
        val statement = connection.createStatement()
        statement.execute("LISTEN ${table.channelName}")

        //NOTE: Polling implementation - poll every second
        while (!isClosedForSend) {
            select {
                onTimeout(1.seconds) {
                    // process all notifications into notifier data
                    val data = pgConnection.notifications
                        .asSequence()
                        .map { createNotifierData(it.parameter) }
                        // filter out possible incorrect data
                        .filterNotNull()
                        .toList()
                    // send out
                    trySend(data)
                }
            }
        }

        awaitClose {
            connection.close()
            statement.close()
        }
    }

    private fun createNotifierData(parameter: String): NotifierData? {
        val json = Json { decodeEnumsCaseInsensitive = true }
        return runCatching { json.decodeFromString<NotifierData>(parameter) }
            .onFailure {
                LOG.warn("Unable to deserialize notifier data", it)
            }
            .getOrNull()
    }

    companion object {
        enum class Table(val channelName: String) {
            KLINK("klink_change"),
        }
    }
}