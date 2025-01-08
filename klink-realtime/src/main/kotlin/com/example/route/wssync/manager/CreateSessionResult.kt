package com.example.route.wssync.manager

import com.example.route.wssync.session.KlinkWsSyncSession

sealed interface CreateSessionResult {
    data object InvalidSessionData: CreateSessionResult

    data object NoAccess: CreateSessionResult

    data class Session(val session: KlinkWsSyncSession): CreateSessionResult
}