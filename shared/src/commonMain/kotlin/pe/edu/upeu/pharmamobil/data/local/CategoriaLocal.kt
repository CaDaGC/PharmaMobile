package pe.edu.upeu.pharmamobil.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upeu.pharmamobil.db.PharmaDb
import pe.edu.upeu.pharmamobil.domain.model.Categoria

class CategoriaLocal(base: PharmaDb) {
    private val consultas = base.categoriaQueries

    fun observar(): Flow<List<Categoria>> =
        consultas.listarTodas { id, nombre, descripcion, estado, pendiente ->
            Categoria(
                id = id,
                nombre = nombre,
                descripcion = descripcion,
                estado = estado == 1L // Convierte el Long de SQLite a Boolean
            )
        }.asFlow()
            .mapToList(Dispatchers.Default)

    fun pendientes(): List<Categoria> =
        consultas.listarPendientes { id, nombre, descripcion, estado, pendiente ->
            Categoria(
                id = id,
                nombre = nombre,
                descripcion = descripcion,
                estado = estado == 1L
            )
        }.executeAsList()

    fun guardarPendiente(categoria: Categoria): Categoria {
        val menor = consultas.siguienteIdLocal().executeAsOne() ?: 0L
        val idLocal = minOf(menor, 0L) - 1L
        val local = categoria.copy(id = idLocal)

        consultas.insertar(
            id = local.id,
            nombre = local.nombre,
            descripcion = local.descripcion,
            estado = if (local.estado) 1L else 0L,
            pendiente = 1L
        )
        return local
    }

    fun reemplazarSincronizados(categorias: List<Categoria>) {
        consultas.transaction {
            consultas.borrarSincronizados()
            categorias.forEach {
                consultas.insertar(
                    id = it.id,
                    nombre = it.nombre,
                    descripcion = it.descripcion,
                    estado = if (it.estado) 1L else 0L,
                    pendiente = 0L
                )
                consultas.borrarPendientePorNombre(it.nombre)
            }
        }
    }
}