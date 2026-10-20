package pe.edu.upeu.pharmamobil.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobil.domain.model.Categoria

interface CategoriaRepository {
    suspend fun listar(): Result<List<Categoria>>
    suspend fun obtener(id: Long): Result<Categoria>
    suspend fun registrar(categoria: Categoria): Categoria
    suspend fun actualizar(categoria: Categoria): Result<Categoria>
    suspend fun eliminar(id: Long): Result<Unit>
    fun observar(): Flow<List<Categoria>>
    suspend fun sincronizar()
}