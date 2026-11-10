package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro

object HttpClientFactory {
    const val BASE_URL = "http://10.0.2.2:8080"

    fun create(
        engine: HttpClientEngine,
        almacen: AlmacenSeguro,
        esDepuracion: Boolean = true,
        baseUrl: String = BASE_URL
    ): HttpClient {
        return HttpClient(engine) {
            configurarClient(almacen, esDepuracion, baseUrl)
        }
    }

    fun create(almacen: AlmacenSeguro, esDepuracion: Boolean = true): HttpClient {
        return HttpClient {
            configurarClient(almacen, esDepuracion, BASE_URL)
        }
    }

    private fun io.ktor.client.HttpClientConfig<*>.configurarClient(
        almacen: AlmacenSeguro,
        esDepuracion: Boolean,
        baseUrl: String
    ) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }

        install(DefaultRequest) {
            val esLogin = url.encodedPath.trim('/').endsWith("auth/login")
            url("$baseUrl/api/v1/")
            contentType(ContentType.Application.Json)
            if (!esLogin) {
                almacen.leerToken()?.let { headers.append(HttpHeaders.Authorization, "Bearer $it") }
            }
        }

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.HEADERS
            level = if (esDepuracion) LogLevel.HEADERS else LogLevel.NONE
            sanitizeHeader { nombre -> nombre == HttpHeaders.Authorization }
        }
        HttpResponseValidator {
            handleResponseExceptionWithRequest { causa, solicitud ->
                val respuesta = (causa as? ClientRequestException)?.response
                if (respuesta?.status == HttpStatusCode.Unauthorized &&
                    !solicitud.url.encodedPath.endsWith("/auth/login")
                ) {
                    almacen.borrarToken()
                    pe.edu.upeu.pharmamobil.SessionManager.invalidateSession()
                }
            }
        }
    }
}