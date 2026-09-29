package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pe.edu.upeu.pharmamobil.data.remote.dto.CategoriaRequestDto
import pe.edu.upeu.pharmamobil.data.remote.dto.CategoriaResponseDto

class CategoriaApiService(private val client: HttpClient) {

    suspend fun getCategorias(): List<CategoriaResponseDto> {
        return client.get("${HttpClientFactory.BASE_URL}/api/v1/categorias").body()
    }

    suspend fun createCategoria(requestDto: CategoriaRequestDto): CategoriaResponseDto {
        return client.post("${HttpClientFactory.BASE_URL}/api/v1/categorias") {
            contentType(ContentType.Application.Json)
            setBody(requestDto)
        }.body()
    }
}