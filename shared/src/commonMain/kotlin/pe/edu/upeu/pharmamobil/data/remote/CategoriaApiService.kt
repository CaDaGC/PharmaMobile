package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import pe.edu.upeu.pharmamobil.data.remote.dto.CategoriaRequestDto
import pe.edu.upeu.pharmamobil.data.remote.dto.CategoriaResponseDto

class CategoriaApiService(private val client: HttpClient) {

    // Cambiado de PaginaResponseDto a List<CategoriaResponseDto>
    suspend fun listar(): List<CategoriaResponseDto> {
        return client.get("${HttpClientFactory.BASE_URL}/api/v1/categorias").body()
    }

    suspend fun obtener(id: Long): CategoriaResponseDto {
        return client.get("${HttpClientFactory.BASE_URL}/api/v1/categorias/$id").body()
    }

    suspend fun crear(request: CategoriaRequestDto): CategoriaResponseDto {
        return client.post("${HttpClientFactory.BASE_URL}/api/v1/categorias") {
            setBody(request)
        }.body()
    }

    suspend fun actualizar(id: Long, request: CategoriaRequestDto): CategoriaResponseDto {
        return client.put("${HttpClientFactory.BASE_URL}/api/v1/categorias/$id") {
            setBody(request)
        }.body()
    }

    suspend fun eliminar(id: Long) {
        client.delete("${HttpClientFactory.BASE_URL}/api/v1/categorias/$id")
    }
}