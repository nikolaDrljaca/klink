package com.example.framework.koin

import com.example.KlinkDatabase
import com.example.data.notifier.KlinkAsyncDatabaseNotifier
import com.example.data.notifier.KlinkDatabaseNotifier
import com.example.data.provideHikariDataSource
import com.example.data.provideKlinkDatabase
import com.example.domain.KlinkSyncProcessor
import com.example.domain.repository.KlinkRepository
import com.example.domain.repository.KlinkRepositoryImpl
import com.example.domain.usecase.CheckKlinkAccess
import com.example.domain.usecase.ObserveKlinkEntries
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun appModules(app: Application): List<Module> {
    val coreModule = module {
        single { provideHikariDataSource(app) }
        single { provideKlinkDatabase(get()) }
        single { app.provideCoroutineScope() }
    }
    return listOf(
        coreModule,
        coroutineModule,
        dataModule(),
        domainModule()
    )
}

fun dataModule() = module {
    single { get<KlinkDatabase>().klinkQueries }
    single { get<KlinkDatabase>().klinkKeyQueries }
    single { get<KlinkDatabase>().klinkEntryQueries }

    single {
        KlinkAsyncDatabaseNotifier(get())
    } bind KlinkDatabaseNotifier::class
}

fun domainModule() = module {
    // repos
    single {
        KlinkRepositoryImpl(
            dispatcher = get(named(CoroutineModuleName.IO)),
            klinkDao = get(),
            klinkEntryDao = get(),
            keysDao = get()
        )
    } bind KlinkRepository::class

    factory {
        ObserveKlinkEntries(
            notifier = get(),
            repository = get()
        )
    }

    factory {
        CheckKlinkAccess(
            dispatcher = get(named(CoroutineModuleName.Default)),
            repo = get()
        )
    }

    factory {
        KlinkSyncProcessor(
            scope = get(),
            repo = get(),
            klinkId = it.get()
        )
    }
}
