package com.example.data

import app.cash.sqldelight.Query
import app.cash.sqldelight.db.SqlDriver

/**
 * A JDBC based SqlDriver that implements very simple `notify listeners` protocol to enable
 * `asFlow` queries to work as expected. Basically implements the observer pattern.
 *
 * The JDBC driver is not reactive and does not implement the LISTEN/NOTIFY protocol that others do (e.g. AndroidDriver)
 */
class JdbcNotifyDriver(
    private val driver: SqlDriver,
) : SqlDriver by driver {
    private val listeners = linkedMapOf<String, MutableSet<Query.Listener>>()

    override fun addListener(vararg queryKeys: String, listener: Query.Listener) {
        synchronized(listeners) {
            queryKeys.forEach { key ->
                val actual = listeners.getOrPut(key) { linkedSetOf() }
                actual.add(listener)
            }
        }
    }

    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) {
        synchronized(listeners) {
            queryKeys.forEach { key ->
                listeners[key]?.remove(listener)
            }
        }
    }

    override fun notifyListeners(vararg queryKeys: String) {
        val toNotify = linkedSetOf<Query.Listener>()
        synchronized(listeners) {
            queryKeys.forEach { key -> listeners[key]?.let(toNotify::addAll) }
        }
        toNotify.forEach(Query.Listener::queryResultsChanged)
    }
}