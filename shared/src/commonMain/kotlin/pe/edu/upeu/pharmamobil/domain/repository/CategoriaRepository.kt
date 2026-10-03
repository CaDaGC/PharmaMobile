package pe.edu.upeu.pharmamobil.domain.repository

import pe.edu.upeu.pharmamobil.domain.model.Categoria

interface CategoriaRepository {
    suspend fun listar(): Result<List<Categoria>>
    suspend fun obtener(id: Long): Result<Categoria>
    suspend fun registrar(categoria: Categoria): Result<Categoria>
    suspend fun actualizar(categoria: Categoria): Result<Categoria>
    suspend fun eliminar(id: Long): Result<Unit>
}