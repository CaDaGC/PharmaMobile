package pe.edu.upeu.pharmamobil.domain.usecase

import kotlinx.coroutines.test.runTest
import pe.edu.upeu.pharmamobil.data.repository.CategoriaRepositoryImpl
import pe.edu.upeu.pharmamobil.domain.model.Categoria
import pe.edu.upeu.pharmamobil.domain.usecase.EliminarCategoriaUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ListarCategoriasUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarCategoriaUseCase
import pe.edu.upeu.pharmamobil.fakes.FakeCategoriaApiService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CategoriaUseCasesTest {

    private lateinit var fakeApi: FakeCategoriaApiService
    private lateinit var repository: CategoriaRepositoryImpl
    private lateinit var listarUseCase: ListarCategoriasUseCase
    private lateinit var registrarUseCase: RegistrarCategoriaUseCase
    private lateinit var eliminarUseCase: EliminarCategoriaUseCase

    @BeforeTest
    fun setUp() {
        fakeApi = FakeCategoriaApiService()
        // CORRECCIÓN: Pasar fakeApi.crearService() para entregar la instancia de CategoriaApiService
        repository = CategoriaRepositoryImpl(fakeApi.crearService())
        listarUseCase = ListarCategoriasUseCase(repository)
        registrarUseCase = RegistrarCategoriaUseCase(repository)
        eliminarUseCase = EliminarCategoriaUseCase(repository)
    }

    @Test
    fun `ListarCategoriasUseCase obtiene el listado correctamente`() = runTest {
        val resultado = listarUseCase()

        assertTrue(resultado.isSuccess)
        assertEquals(2, resultado.getOrNull()?.size)
    }

    @Test
    fun `RegistrarCategoriaUseCase guarda una nueva categoria`() = runTest {
        val categoria = Categoria(id = 0L, nombre = "Dermatología", descripcion = "Cuidado de la piel")
        val resultado = registrarUseCase(categoria)

        assertTrue(resultado.isSuccess)
        assertEquals("Dermatología", resultado.getOrNull()?.nombre)
    }

    @Test
    fun `EliminarCategoriaUseCase propaga el fallo de eliminacion`() = runTest {
        val resultado = eliminarUseCase(1L)

        assertTrue(resultado.isFailure)
    }
}