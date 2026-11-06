package pe.edu.upeu.pharmamobil

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.data.local.CategoriaLocal
import pe.edu.upeu.pharmamobil.data.remote.CategoriaApiService
import pe.edu.upeu.pharmamobil.data.remote.HttpClientFactory
import pe.edu.upeu.pharmamobil.data.repository.CategoriaRepositoryImpl
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.model.Categoria
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CategoriaOfflineTest {

    private fun baseEnMemoria(): PharmaDb {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        PharmaDb.Schema.create(driver)
        return PharmaDb(driver)
    }

    private fun crearMockClient(
        jsonResponse: String,
        status: HttpStatusCode = HttpStatusCode.OK,
        postStatus: HttpStatusCode = status
    ): HttpClient {
        return HttpClient(
            MockEngine { request ->
                val responseContent = if (request.method == HttpMethod.Post) {
                    """{"id": 99, "nombre": "Registrado", "descripcion": "Subido"}"""
                } else {
                    jsonResponse
                }
                respond(
                    content = responseContent,
                    status = if (request.method == HttpMethod.Post) postStatus else status,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            },
        ) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Test
    fun prueba1_sincronizacionConRed() = runBlocking {
        val db = baseEnMemoria()
        val local = CategoriaLocal(db)
        val json = """[{"id": 1, "nombre": "Analgésicos", "descripcion": "Para el dolor"}]"""
        val api = CategoriaApiService(crearMockClient(json))
        val repo = CategoriaRepositoryImpl(local, api)

        repo.listar()
        val resultado = repo.observar().first()

        assertEquals(1, resultado.size)
        assertEquals("Analgésicos", resultado[0].nombre)
    }

    @Test
    fun prueba2_registroSinRed() = runBlocking {
        val db = baseEnMemoria()
        val local = CategoriaLocal(db)
        val api = CategoriaApiService(crearMockClient("", HttpStatusCode.InternalServerError))
        val repo = CategoriaRepositoryImpl(local, api)

        val nueva = Categoria(id = 0, nombre = "Antibióticos", descripcion = "Infecciones")
        repo.registrar(nueva)

        val resultado = repo.observar().first()
        assertEquals(1, resultado.size)
        assertTrue(resultado[0].pendiente)
        assertTrue(resultado[0].id < 0, "El ID local debe ser negativo para marcarse como pendiente")
    }

    @Test
    fun prueba3_envioAlReconectar() = runBlocking {
        val db = baseEnMemoria()
        val local = CategoriaLocal(db)
        val nueva = Categoria(id = 0, nombre = "Vitaminas", descripcion = "Suplementos")
        local.guardarPendiente(nueva)

        val json = """[{"id": 10, "nombre": "Vitaminas", "descripcion": "Suplementos"}]"""
        val api = CategoriaApiService(crearMockClient(json))
        val repo = CategoriaRepositoryImpl(local, api)

        repo.sincronizar()

        val resultado = repo.observar().first()
        assertTrue(resultado.none { it.pendiente })
        assertTrue(resultado.all { it.id > 0 }, "Todos los IDs deben ser positivos tras sincronizar")
    }

    @Test
    fun prueba4_laBajadaNoBorraLoPendiente() = runBlocking {
        val db = baseEnMemoria()
        val local = CategoriaLocal(db)
        local.guardarPendiente(Categoria(id = 0, nombre = "Pendiente Local", descripcion = "Sin red"))

        val json = """[{"id": 2, "nombre": "Servidor Cat", "descripcion": "Desde API"}]"""
        val api = CategoriaApiService(crearMockClient(json, postStatus = HttpStatusCode.ServiceUnavailable))
        val repo = CategoriaRepositoryImpl(local, api)

        repo.listar()

        val resultado = repo.observar().first()
        assertEquals(2, resultado.size, "Debe conservar la local pendiente y la del servidor")
    }

    @Test
    fun prueba5_identificadoresSinColision() = runBlocking {
        val db = baseEnMemoria()
        val local = CategoriaLocal(db)
        local.reemplazarSincronizados(listOf(Categoria(id = 1, nombre = "Remoto")))
        local.guardarPendiente(Categoria(nombre = "Primera local"))
        local.guardarPendiente(Categoria(nombre = "Segunda local"))

        val resultado = local.observar().first()
        assertEquals(setOf(-2L, -1L, 1L), resultado.map { it.id }.toSet())
        assertEquals(2, resultado.count { it.pendiente })
    }

    @Test
    fun prueba6_sincronizarDosVecesNoDuplica() = runBlocking {
        val db = baseEnMemoria()
        val local = CategoriaLocal(db)
        val json = """[{"id": 1, "nombre": "Analgésicos", "descripcion": "Para el dolor"}]"""
        val api = CategoriaApiService(crearMockClient(json))
        val repo = CategoriaRepositoryImpl(local, api)

        repo.listar()
        repo.listar()

        val resultado = repo.observar().first()
        assertEquals(1, resultado.size, "Sincronizar dos veces debe mantener 1 sola fila")
    }

    @Test
    fun cambiarCuentaUsaElTokenActualYUn401CierraLaSesion() = runBlocking {
        val almacen = object : AlmacenSeguro {
            private var token: String? = "admin"
            override fun leerToken() = token
            override fun guardarToken(token: String) { this.token = token }
            override fun borrarToken() { token = null }
        }
        val cabeceras = mutableListOf<String?>()
        val cliente = HttpClientFactory.create(
            engine = MockEngine { request ->
                val autorizacion = request.headers[HttpHeaders.Authorization]
                cabeceras += autorizacion
                respond(
                    content = "[]",
                    status = if (autorizacion == "Bearer vendedor") {
                        HttpStatusCode.Unauthorized
                    } else {
                        HttpStatusCode.OK
                    },
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            },
            almacen = almacen,
            esDepuracion = false
        )
        SessionManager.resetSession()
        try {
            val api = CategoriaApiService(cliente)
            api.listar()
            almacen.guardarToken("vendedor")
            val error = runCatching { api.listar() }.exceptionOrNull()
            assertTrue(error is io.ktor.client.plugins.ClientRequestException)
            assertEquals<List<String?>>(listOf("Bearer admin", "Bearer vendedor"), cabeceras)
            assertNull(almacen.leerToken())
            assertFalse(SessionManager.isSessionValid.value)
        } finally {
            cliente.close()
            SessionManager.resetSession()
        }
    }
}
