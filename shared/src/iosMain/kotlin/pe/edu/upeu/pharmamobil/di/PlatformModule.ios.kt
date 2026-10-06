package pe.edu.upeu.pharmamobil.di

import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import pe.edu.upeu.pharmamobil.data.remote.HttpClientFactory
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.platform.CompartidorIos

actual val platformModule: Module = module {
    single<Compartidor> { CompartidorIos() }
    single { HttpClientFactory.create(Darwin.create()) }
}
