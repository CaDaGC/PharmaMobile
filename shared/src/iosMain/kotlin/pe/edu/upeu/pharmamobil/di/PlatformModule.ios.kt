package pe.edu.upeu.pharmamobil.di


import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguroIos
import pe.edu.upeu.pharmamobil.data.remote.HttpClientFactory
import pe.edu.upeu.pharmamobil.data.repository.AuthRepositorioRest
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository
import pe.edu.upeu.pharmamobil.platform.CompartidorIos

val iosModule = module {
    single<Compartidor> { CompartidorIos() }
    single<AlmacenSeguro> { AlmacenSeguroIos() }
    single<AuthRepository> { AuthRepositorioRest(get(), get()) }
    single {
        HttpClientFactory.create(
            engine = Darwin.create(),
            almacen = get(),
            esDepuracion = true
        )
    }
}