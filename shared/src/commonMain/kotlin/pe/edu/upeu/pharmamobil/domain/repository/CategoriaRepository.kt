package pe.edu.upeu.pharmamobil.domain.repository

import pe.edu.upeu.pharmamobil.domain.model.Categoria

interface CategoriaRepository {
    suspend fun getCategorias(): Result<List<Categoria>>
    suspend fun createCategoria(categoria: Categoria): Result<Categoria>
}