package com.example.framework.koin

import com.example.data.provideKlinkDatabase
import io.ktor.server.application.*
import org.koin.dsl.module

fun appModules(app: Application) = module {
    single { provideKlinkDatabase(app) }
}