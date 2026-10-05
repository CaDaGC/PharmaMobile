package pe.edu.upeu.pharmamobil.fakes

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import pe.edu.upeu.pharmamobil.data.remote.CategoriaApiService

class FakeCategoriaApiService {

    var simularErrorRed: Boolean = false
    var codigoErrorSimulado: HttpStatusCode = HttpStatusCode.Conflict
    var mensajeErrorSimulado: String = "No se puede eliminar la categoría porque tiene productos asociados"

    fun crearService(): CategoriaApiService {
        val mockEngine = MockEngine { request ->
            val path = request.url.encodedPath
            val method = request.method

            if (simularErrorRed) {
                return@MockEngine respond(
                    content = """{"message": "$mensajeErrorSimulado"}""",
                    status = codigoErrorSimulado,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            when (method) {
                HttpMethod.Get -> {
                    val jsonResponse = """
                        [
                            {"id": 1, "nombre": "Respiratorio", "descripcion": "Para problemas del sistema respiratorio"},
                            {"id": 2, "nombre": "Digestivo", "descripcion": "Para problemas del sistema digestivo"}
                        ]
                    """.trimIndent()
                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

                HttpMethod.Post -> {
                    val bodyString = request.body.toByteArray().decodeToString()
                    val nombre = try {
                        val jsonElement = Json.parseToJsonElement(bodyString)
                        jsonElement.jsonObject["nombre"]?.jsonPrimitive?.content ?: "Analgésicos"
                    } catch (e: Exception) {
                        "Analgésicos"
                    }
                    val descripcion = try {
                        val jsonElement = Json.parseToJsonElement(bodyString)
                        jsonElement.jsonObject["descripcion"]?.jsonPrimitive?.content ?: "Descripción"
                    } catch (e: Exception) {
                        "Descripción"
                    }

                    val jsonResponse = """
                        {"id": 3, "nombre": "$nombre", "descripcion": "$descripcion"}
                    """.trimIndent()

                    respond(
                        content = jsonResponse,
                        status = HttpStatusCode.Created,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

                HttpMethod.Delete -> {
                    if (path.endsWith("1") || path.endsWith("/1")) {
                        respond(
                            content = """{"message": "No se puede eliminar la categoría porque tiene productos asociados"}""",
                            status = HttpStatusCode.Conflict,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    } else {
                        respond(
                            content = "",
                            status = HttpStatusCode.NoContent,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    }
                }

                else -> {
                    respond(
                        content = "{}",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
        }

        val client = HttpClient(mockEngine) {
            defaultRequest {
                url("http://localhost:8080/")
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }

        return CategoriaApiService(client)
    }
}