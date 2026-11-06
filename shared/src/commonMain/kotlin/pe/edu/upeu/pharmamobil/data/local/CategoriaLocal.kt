package pe.edu.upeu.pharmamobil.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobil.data.mapper.toDomain
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.model.Categoria

class CategoriaLocal(private val db: PharmaDb) {
    private val queries = db.categoriaQueries

    fun observar(): Flow<List<Categoria>> =
        queries.listarTodas { id, nombre, descripcion, estado, pendiente, intentos, ultimoError ->
            Categoria(id = id, nombre = nombre, descripcion = descripcion, estado = estado == 1L, pendiente = pendiente == 1L)
        }.asFlow().mapToList(Dispatchers.IO)

    fun listarTodas(): List<Categoria> =
        queries.listarTodas { id, nombre, descripcion, estado, pendiente, intentos, ultimoError ->
            Categoria(id = id, nombre = nombre, descripcion = descripcion, estado = estado == 1L, pendiente = pendiente == 1L)
        }.executeAsList()

    fun listarPendientes(maxIntentos: Long): List<pe.edu.upeu.pharmamobil.Categoria> =
        queries.listarPendientes(maxIntentos).executeAsList()

    fun guardarPendiente(categoria: Categoria): Categoria = db.transactionWithResult {
        val minimoId = queries.siguienteIdLocal().executeAsOne()
        val idLocal = minOf(minimoId, 0L) - 1L

        queries.insertar(
            id = idLocal,
            nombre = categoria.nombre,
            descripcion = categoria.descripcion,
            estado = if (categoria.estado) 1L else 0L,
            pendiente = 1L,
            intentos = 0L,
            ultimoError = null
        )
        categoria.copy(id = idLocal, pendiente = true)
    }

    fun sumarIntento(id: Long, ultimoError: String) {
        queries.sumarIntento(ultimoError = ultimoError, id = id)
    }

    fun marcarComoFallida(id: Long, intentos: Long, error: String) {
        queries.marcarComoFallida(intentos = intentos, error = error, id = id)
    }

    fun confirmarSincronizacion(idLocal: Long, idServidor: Long) {
        queries.confirmarSincronizacion(idServidor = idServidor, idLocal = idLocal)
    }

    fun reemplazarSincronizados(remotas: List<Categoria>) = db.transaction {
        queries.reemplazarSincronizados()
        val idsPendientes = queries.idsPendientes().executeAsList().toSet()
        remotas.filterNot { it.id in idsPendientes }.forEach { cat ->
            queries.insertar(
                id = cat.id,
                nombre = cat.nombre,
                descripcion = cat.descripcion,
                estado = if (cat.estado) 1L else 0L,
                pendiente = 0L,
                intentos = 0L,
                ultimoError = null
            )
        }
    }
}