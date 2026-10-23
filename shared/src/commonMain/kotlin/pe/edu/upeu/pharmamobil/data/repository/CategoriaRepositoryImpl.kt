package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobil.data.local.CategoriaLocal
import pe.edu.upeu.pharmamobil.data.local.SincronizacionPreferences
import pe.edu.upeu.pharmamobil.data.mapper.toDomain
import pe.edu.upeu.pharmamobil.data.mapper.toRequest
import pe.edu.upeu.pharmamobil.data.remote.CategoriaApiService
import pe.edu.upeu.pharmamobil.data.remote.ejecutarLlamada
import pe.edu.upeu.pharmamobil.domain.error.ErrorApi
import pe.edu.upeu.pharmamobil.domain.error.ErrorApiException
import pe.edu.upeu.pharmamobil.domain.model.Categoria
import pe.edu.upeu.pharmamobil.domain.repository.CategoriaRepository

class CategoriaRepositoryImpl(
    private val local: CategoriaLocal,
    private val api: CategoriaApiService,
    private val preferenciasSync: SincronizacionPreferences = SincronizacionPreferences() // <-- Inyectado o instanciado
) : CategoriaRepository {

    override fun observar(): Flow<List<Categoria>> = local.observar()

    // Método adicional para exponer el texto de la antigüedad a la UI
    fun obtenerTextoAntiguedad(): String = preferenciasSync.obtenerTextoUltimaSincronizacion()

    override suspend fun listar(): Result<List<Categoria>> = ejecutarLlamada {
        try {
            sincronizar()
        } catch (e: Exception) {
            // Si no hay red, ignoramos el fallo para seguir mostrando la base local
        }
        api.listar().map { it.toDomain() }
    }

    override suspend fun obtener(id: Long): Result<Categoria> = ejecutarLlamada {
        api.obtener(id).toDomain()
    }

    override suspend fun registrar(categoria: Categoria): Categoria {
        val guardado = local.guardarPendiente(categoria)
        intentar { sincronizar() }
        return guardado
    }

    override suspend fun actualizar(categoria: Categoria): Result<Categoria> = ejecutarLlamada {
        val resultado = api.actualizar(categoria.id, categoria.toRequest()).toDomain()
        sincronizar()
        resultado
    }

    override suspend fun eliminar(id: Long): Result<Unit> = ejecutarLlamada {
        api.eliminar(id)
        sincronizar()
    }

    override suspend fun sincronizar() {
        subirPendientes()
        val listaRemota = api.listar().map { it.toDomain() }
        local.reemplazarSincronizados(listaRemota)

        // ¡REGISTRAMOS EL ÉXITO DE LA SINCRONIZACIÓN AQUÍ!
        preferenciasSync.registrarSincronizacionExitosa()
    }

    private suspend fun subirPendientes() {
        local.pendientes().forEach { pendiente ->
            api.crear(pendiente.toRequest())
        }
    }

    private suspend fun intentar(bloque: suspend () -> Unit) {
        try {
            bloque()
        } catch (e: ErrorApiException) {
            if (e.errorApi is ErrorApi.SinConexion || e.errorApi is ErrorApi.TiempoAgotado) {
                // Silenciamos el error de red
            } else {
                throw e
            }
        } catch (e: Exception) {
            // Excepción genérica
        }
    }
}