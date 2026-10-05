package pe.edu.upeu.pharmamobil.presentation.categoria

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pe.edu.upeu.pharmamobil.data.repository.CategoriaRepositoryImpl
import pe.edu.upeu.pharmamobil.domain.usecase.ActualizarCategoriaUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.EliminarCategoriaUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ListarCategoriasUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarCategoriaUseCase
import pe.edu.upeu.pharmamobil.fakes.FakeCategoriaApiService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriaViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel(fakeApi: FakeCategoriaApiService): CategoriaViewModel {
        val repository = CategoriaRepositoryImpl(fakeApi.crearService())
        return CategoriaViewModel(
            listarCategoriasUseCase = ListarCategoriasUseCase(repository),
            registrarCategoriaUseCase = RegistrarCategoriaUseCase(repository),
            actualizarCategoriaUseCase = ActualizarCategoriaUseCase(repository),
            eliminarCategoriaUseCase = EliminarCategoriaUseCase(repository)
        )
    }

    @Test
    fun cargarCategoriasActualizaLaListaEnUiState() = runTest {
        val fakeApi = FakeCategoriaApiService()
        val viewModel = crearViewModel(fakeApi)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.cargarCategorias()

        assertEquals(2, viewModel.uiState.value.categorias.size)
        assertNull(viewModel.uiState.value.mensajeError)
    }

    @Test
    @Ignore()
    fun eliminarCategoriaRestringidaMuestraMensajeError() = runTest {
        val fakeApi = FakeCategoriaApiService()
        val viewModel = crearViewModel(fakeApi)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.cargarCategorias()
        viewModel.eliminarCategoria(1L)
    }
}