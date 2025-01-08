package com.example.route.wssync.manager

import arrow.core.raise.catch
import arrow.core.raise.either
import com.example.LOG
import com.example.domain.model.KlinkAccessType
import com.example.domain.model.KlinkKey
import com.example.domain.model.KlinkKeys
import com.example.domain.usecase.CheckKlinkAccess
import com.example.route.wssync.KlinkWsSyncSessionData
import com.example.route.wssync.session.KlinkWsSyncSession
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class KlinkWsSyncSessionManager : KoinComponent {
    private val sessions = ConcurrentHashMap<KlinkWsSyncSessionData, KlinkWsSyncSession>()

    private val runAccessProbe: CheckKlinkAccess by inject()

    suspend fun create(data: KlinkWsSyncSessionData): CreateSessionResult {
        // check if session exists
        if (sessions.containsKey(data)) {
            val existingSession = sessions[data]!!
            LOG.info("Attaching existing session for ${data.klinkId}")
            return CreateSessionResult.Session(existingSession)
        }
        // create new session
        LOG.info("Creating session for ${data.klinkId}")
        val accessProbe = either {
            val id = catch({ UUID.fromString(data.klinkId) }) {
                LOG.warn("Passed klink ID is not a valid UUID! - ${data.klinkId}")
                raise("Invalid UUID!")
            }
            KlinkKeys(
                klinkId = id,
                readKey = KlinkKey(data.readKey).bind(),
                writeKey = data.writeKey?.let { KlinkKey(it).bind() }
            )
        }
        if (accessProbe.isLeft()) {
            return CreateSessionResult.InvalidSessionData
        }
        val probe = accessProbe.getOrNull()!!
        val accessType = runAccessProbe.execute(probe)
        // if no access type is granted, don't create session and return out
        if (accessType == KlinkAccessType.NO_ACCESS) {
            return CreateSessionResult.NoAccess
        }
        // create session
        val session = KlinkWsSyncSession(
            klinkId = data.klinkId,
            isReadOnly = accessType == KlinkAccessType.READ_ACCESS,
            syncProcessor = this.get { parametersOf(data.klinkId) },
            observeKlinkEntries = this.get(),
            databaseNotifier = this.get()
        )
        // assign
        sessions[data] = session
        // return out
        return CreateSessionResult.Session(session)
    }

    fun remove(sessionData: KlinkWsSyncSessionData) = sessions.remove(sessionData)
}