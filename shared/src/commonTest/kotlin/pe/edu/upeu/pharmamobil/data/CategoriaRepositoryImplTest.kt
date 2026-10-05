package pe.edu.upeu.pharmamobil.data

import kotlinx.coroutines.test.runTest
import pe.edu.upeu.pharmamobil.data.repository.CategoriaRepositoryImpl
import pe.edu.upeu.pharmamobil.domain.model.Categoria
import pe.edu.upeu.pharmamobil.fakes.FakeCategoriaApiService
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CategoriaRepositoryImplTest {

    private lateinit var fakeApi: FakeCategoriaApiService
    private lateinit var repository: CategoriaRepositoryImpl

    @BeforeTest
    fun setUp() {
        fakeApi = FakeCategoriaApiService()
        repository = CategoriaRepositoryImpl(fakeApi.crearService())
    }

    @Test
    fun `listar categorias retorna lista exitosa`() = runTest {
        val resultado = repository.listar()

        assertTrue(resultado.isSuccess)
        assertEquals(2, resultado.getOrNull()?.size)
    }

    @Test
    fun `registrar categoria retorna objeto registrado`() = runTest {
        val nuevaCat = Categoria(id = 0L, nombre = "Analgésicos", descripcion = "Para el dolor")
        val resultado = repository.registrar(nuevaCat)

        assertTrue(resultado.isSuccess)
        assertEquals("Analgésicos", resultado.getOrNull()?.nombre)
    }

    @Test
    fun `eliminar categoria restringida retorna fallo 409`() = runTest {
        val resultado = repository.eliminar(1L)

        assertTrue(resultado.isFailure)
    }
}