package com.example.framework.koin

import com.example.KlinkDatabase
import com.example.data.KlinkRepository
import com.example.data.KlinkRepositoryImpl
import com.example.data.provideKlinkDatabase
import com.example.domain.usecase.CheckKlinkAccess
import com.example.domain.usecase.GetKlinkKeys
import com.example.domain.usecase.ObserveKlinkEntries
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

fun appModules(app: Application): List<Module> {
    val coreModule = module {
        single { provideKlinkDatabase(app) }
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

    // repos
    single {
        KlinkRepositoryImpl(
            dispatcher = get(named(CoroutineModuleName.IO)),
            klinkDao = get(),
            klinkEntryDao = get(),
            keysDao = get()
        )
    } bind KlinkRepository::class
}

fun domainModule() = module {
    factory { CheckKlinkAccess(get()) }
    factory { ObserveKlinkEntries(get()) }
    factory {
        val dao: KlinkRepository = get()
        GetKlinkKeys { id -> dao.findKeysByKlinkId(id) }
    }
}
