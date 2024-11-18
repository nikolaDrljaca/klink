package com.example.framework.koin

import com.example.KlinkDatabase
import com.example.KlinkKeyQueries
import com.example.data.GetKlinkKeys
import com.example.data.provideKlinkDatabase
import com.example.domain.CheckKlinkAccess
import com.example.domain.KlinkValueFlow
import io.ktor.server.application.*
import org.koin.core.module.Module
import org.koin.dsl.module

fun appModules(app: Application): List<Module> {
    val coreModule = module {
        single { provideKlinkDatabase(app) }
    }
    return listOf(
        coreModule,
        dataModule(),
        domainModule()
    )
}

fun dataModule() = module {
    single { get<KlinkDatabase>().klinkQueries }
    single { get<KlinkDatabase>().klinkKeyQueries }
    single { get<KlinkDatabase>().klinkEntryQueries }

    // data fetchers
    single {
        val dao: KlinkKeyQueries = get()
        GetKlinkKeys { id ->
            dao.findByKlinkId(id).executeAsOneOrNull()
        }
    }
}

fun domainModule() = module {
    factory { CheckKlinkAccess(get()) }
    factory { KlinkValueFlow(get()) }
}
