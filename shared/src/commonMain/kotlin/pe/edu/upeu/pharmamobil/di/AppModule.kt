package pe.edu.upeu.pharmamobil.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.data.remote.CategoriaApiService
import pe.edu.upeu.pharmamobil.data.repository.CategoriaRepositoryImpl
import pe.edu.upeu.pharmamobil.data.repository.ClienteRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.domain.repository.CategoriaRepository
import pe.edu.upeu.pharmamobil.domain.repository.ClienteRepository
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.ListarClientesUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ListarProductosUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarClienteUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarProductoUseCase
import pe.edu.upeu.pharmamobil.presentation.categoria.CategoriaViewModel
import pe.edu.upeu.pharmamobil.presentation.cliente.ClienteViewModel
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoViewModel

val dataModule = module {
    // Servicio API utiliza el HttpClient inyectado desde platformModule
    single { CategoriaApiService(get()) }

    // Repositorio de la API
    single<CategoriaRepository> { CategoriaRepositoryImpl(get()) }

    // Repositorios locales en memoria existentes
    single<ProductoRepository> { ProductoRepositorioEnMemoria() }
    single<ClienteRepository> { ClienteRepositorioEnMemoria() }
}

val domainModule = module {
    factory { RegistrarProductoUseCase(get()) }
    factory { ListarProductosUseCase(get()) }
    factory { RegistrarClienteUseCase(get()) }
    factory { ListarClientesUseCase(get()) }
}

val presentationModule = module {
    viewModel { CategoriaViewModel(get()) }
    viewModel { ProductoViewModel(get(), get()) }
    viewModel { ClienteViewModel(get(), get()) }
}

expect val platformModule: Module

fun initKoin(configuracionAdicional: KoinApplication.() -> Unit = {}) {
    startKoin {
        configuracionAdicional()
        modules(
            dataModule,
            domainModule,
            presentationModule,
            platformModule
        )
    }
}
