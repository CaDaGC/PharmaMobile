package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro

object HttpClientFactory {
    const val BASE_URL = "http://10.0.2.2:8080"

    fun create(engine: HttpClientEngine, almacen: AlmacenSeguro, esDepuracion: Boolean = true): HttpClient {
        return HttpClient(engine) {
            configurarClient(almacen, esDepuracion)
        }
    }

    fun create(almacen: AlmacenSeguro, esDepuracion: Boolean = true): HttpClient {
        return HttpClient {
            configurarClient(almacen, esDepuracion)
        }
    }

    private fun io.ktor.client.HttpClientConfig<*>.configurarClient(
        almacen: AlmacenSeguro,
        esDepuracion: Boolean
    ) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }

        install(DefaultRequest) {
            url("$BASE_URL/api/v1/")
            contentType(ContentType.Application.Json)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    almacen.leerToken()?.let { BearerTokens(it, "") }
                }
                sendWithoutRequest { peticion ->
                    !peticion.url.encodedPath.contains("/auth/login")
                }
            }
        }

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.HEADERS
            level = if (esDepuracion) LogLevel.HEADERS else LogLevel.NONE
            sanitizeHeader { nombre -> nombre == HttpHeaders.Authorization }
        }
    }
}