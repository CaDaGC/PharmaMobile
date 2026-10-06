package pe.edu.upeu.pharmamobil.di

import org.koin.core.module.Module
import org.koin.dsl.module
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import pe.edu.upeu.pharmamobil.data.remote.HttpClientFactory
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.platform.CompartidorAndroid

actual val platformModule: Module = module {
    single<Compartidor> { CompartidorAndroid(androidContext())}
    single { HttpClientFactory.create(OkHttp.create()) }
}