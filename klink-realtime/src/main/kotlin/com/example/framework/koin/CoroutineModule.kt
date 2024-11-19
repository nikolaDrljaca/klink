package com.example.framework.koin

import io.ktor.server.application.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.qualifier.named
import org.koin.dsl.module

object CoroutineModuleName {
    const val IO = "IODispatcher"
    const val Default = "DefaultDispatcher"
}

val coroutineModule = module {
    single(named(CoroutineModuleName.IO)) { Dispatchers.IO }
    single(named(CoroutineModuleName.Default)) { Dispatchers.Default }
}

/**
 * Creates a coroutine scope bound to the application lifecycle.
 *
 * Scope is intended to be used with [com.example.domain.KlinkSocketEventProcessor], and should not be
 * injected everywhere to avoid overuse and bottlenecks from scope doing too much.
 */
fun Application.provideCoroutineScope(): CoroutineScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    monitor.subscribe(ApplicationStopped) {
        scope.cancel()
    }
    return scope
}
