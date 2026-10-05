package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    const val BASE_URL = "http://10.0.2.2:8080"

    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            configurarClient()
        }
    }

    fun create(): HttpClient {
        return HttpClient {
            configurarClient()
        }
    }

    private fun io.ktor.client.HttpClientConfig<*>.configurarClient() {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }
}