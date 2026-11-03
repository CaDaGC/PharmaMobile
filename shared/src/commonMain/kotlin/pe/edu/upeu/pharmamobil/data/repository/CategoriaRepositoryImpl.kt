package pe.edu.upeu.pharmamobil.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import pe.edu.upeu.pharmamobil.data.local.CategoriaLocal
import pe.edu.upeu.pharmamobil.data.local.SincronizacionPreferences
import pe.edu.upeu.pharmamobil.data.mapper.toDomain
import pe.edu.upeu.pharmamobil.data.mapper.toRequest
import pe.edu.upeu.pharmamobil.data.remote.CategoriaApiService
import pe.edu.upeu.pharmamobil.data.remote.dto.CategoriaRequestDto
import pe.edu.upeu.pharmamobil.data.remote.ejecutarLlamada
import pe.edu.upeu.pharmamobil.domain.error.ErrorApi
import pe.edu.upeu.pharmamobil.domain.error.ErrorApiException
import pe.edu.upeu.pharmamobil.domain.model.Categoria
import pe.edu.upeu.pharmamobil.domain.model.ResultadoEnvio
import pe.edu.upeu.pharmamobil.domain.repository.CategoriaRepository

class CategoriaRepositoryImpl(
    private val local: CategoriaLocal,
    private val api: CategoriaApiService,
    private val preferenciasSync: SincronizacionPreferences = SincronizacionPreferences()
) : CategoriaRepository {

    companion object {
        private const val MAX_INTENTOS = 5L
    }

    override fun observar(): Flow<List<Categoria>> = local.observar()

    fun obtenerTextoAntiguedad(): String = preferenciasSync.obtenerTextoUltimaSincronizacion()

    override suspend fun listar(): Result<List<Categoria>> = ejecutarLlamada {
        try {
            sincronizar()
        } catch (_: Exception) {
            // Ignorar red caída para proveer datos locales
        }
        local.listarTodas()
    }

    override suspend fun obtener(id: Long): Result<Categoria> = ejecutarLlamada {
        api.obtener(id).toDomain()
    }

    override suspend fun registrar(categoria: Categoria): Categoria {
        val guardado = local.guardarPendiente(categoria)
        try {
            sincronizar()
        } catch (_: Exception) {
            // Silenciar errores offline
        }
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
        subirPendientesRefinado()
        val listaRemota = api.listar().map { it.toDomain() }
        local.reemplazarSincronizados(listaRemota)
        preferenciasSync.registrarSincronizacionExitosa()
    }

    private suspend fun subirPendientesRefinado() {
        val pendientes = local.listarPendientes(MAX_INTENTOS)
        pendientes.forEach { fila ->
            val resultadoEnvio = try {
                val respuestaDto = api.crear(
                    CategoriaRequestDto(
                        nombre = fila.nombre,
                        descripcion = fila.descripcion,
                        estado = fila.estado == 1L
                    )
                )
                ResultadoEnvio.Exito(respuestaDto.id ?: 0L)
            } catch (e: ErrorApiException) {
                when (e.errorApi) {
                    is ErrorApi.SinConexion, is ErrorApi.TiempoAgotado, is ErrorApi.Servidor ->
                        ResultadoEnvio.Transitorio(e.message ?: "Error de red transitorio")
                    else ->
                        ResultadoEnvio.Permanente(e.message ?: "Error permanente de validación")
                }
            } catch (e: Exception) {
                ResultadoEnvio.Transitorio(e.message ?: "Excepción desconocida")
            }

            when (resultadoEnvio) {
                is ResultadoEnvio.Exito -> {
                    local.confirmarSincronizacion(
                        idServidor = resultadoEnvio.idServidor,
                        idLocal = fila.id
                    )
                }
                is ResultadoEnvio.Permanente -> {
                    local.marcarComoFallida(
                        id = fila.id,
                        intentos = fila.intentos ?: MAX_INTENTOS,
                        error = resultadoEnvio.motivo
                    )
                }
                is ResultadoEnvio.Transitorio -> {
                    local.sumarIntento(
                        id = fila.id,
                        ultimoError = resultadoEnvio.motivo
                    )
                    delay(calcularEspera(fila.intentos ?: 0L))
                }
            }
        }
    }

    private fun calcularEspera(intentos: Long): Long =
        (1000L shl intentos.coerceAtMost(4).toInt())
}