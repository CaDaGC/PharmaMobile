package pe.edu.upeu.pharmamobil.di

import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import pe.edu.upeu.pharmamobil.data.remote.HttpClientFactory

actual val platformModule: Module = module {
    // Sin dependencias exclusivas de iOS por ahora.
    single { HttpClientFactory.create(Darwin.create()) }
}
