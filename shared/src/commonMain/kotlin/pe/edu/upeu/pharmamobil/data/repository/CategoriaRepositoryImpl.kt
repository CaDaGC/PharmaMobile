package pe.edu.upeu.pharmamobil.data.repository

import pe.edu.upeu.pharmamobil.data.remote.CategoriaApiService
import pe.edu.upeu.pharmamobil.data.remote.dto.toDomain
import pe.edu.upeu.pharmamobil.data.remote.dto.toRequestDto
import pe.edu.upeu.pharmamobil.domain.model.Categoria
import pe.edu.upeu.pharmamobil.domain.repository.CategoriaRepository

class CategoriaRepositoryImpl(
    private val apiService: CategoriaApiService
) : CategoriaRepository {

    override suspend fun getCategorias(): Result<List<Categoria>> {
        return runCatching {
            apiService.getCategorias().map { it.toDomain() }
        }
    }

    override suspend fun createCategoria(categoria: Categoria): Result<Categoria> {
        return runCatching {
            val responseDto = apiService.createCategoria(categoria.toRequestDto())
            responseDto.toDomain()
        }
    }
}