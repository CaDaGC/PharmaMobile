package pe.edu.upeu.pharmamobil.data.repository

import pe.edu.upeu.pharmamobil.data.mapper.toDomain
import pe.edu.upeu.pharmamobil.data.mapper.toRequest
import pe.edu.upeu.pharmamobil.data.remote.CategoriaApiService
import pe.edu.upeu.pharmamobil.data.remote.ejecutarLlamada
import pe.edu.upeu.pharmamobil.domain.model.Categoria
import pe.edu.upeu.pharmamobil.domain.repository.CategoriaRepository

class CategoriaRepositoryImpl(
    private val api: CategoriaApiService
) : CategoriaRepository {

    override suspend fun listar(): Result<List<Categoria>> = ejecutarLlamada {
        api.listar().map { it.toDomain() }
    }

    override suspend fun obtener(id: Long): Result<Categoria> = ejecutarLlamada {
        api.obtener(id).toDomain()
    }

    override suspend fun registrar(categoria: Categoria): Result<Categoria> = ejecutarLlamada {
        api.crear(categoria.toRequest()).toDomain()
    }

    override suspend fun actualizar(categoria: Categoria): Result<Categoria> = ejecutarLlamada {
        api.actualizar(categoria.id, categoria.toRequest()).toDomain()
    }

    override suspend fun eliminar(id: Long): Result<Unit> = ejecutarLlamada {
        api.eliminar(id)
    }
}