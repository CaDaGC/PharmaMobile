package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pe.edu.upeu.pharmamobil.data.remote.dto.CategoriaRequestDto
import pe.edu.upeu.pharmamobil.data.remote.dto.CategoriaResponseDto

open class CategoriaApiService(private val client: HttpClient) {

    open suspend fun listar(): List<CategoriaResponseDto> {
        return client.get("categorias").body()
    }

    open suspend fun obtener(id: Long): CategoriaResponseDto {
        return client.get("categorias/$id").body()
    }

    open suspend fun crear(request: CategoriaRequestDto): CategoriaResponseDto {
        return client.post("categorias") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    open suspend fun actualizar(id: Long, request: CategoriaRequestDto): CategoriaResponseDto {
        return client.put("categorias/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    open suspend fun eliminar(id: Long) {
        client.delete("categorias/$id")
    }
}