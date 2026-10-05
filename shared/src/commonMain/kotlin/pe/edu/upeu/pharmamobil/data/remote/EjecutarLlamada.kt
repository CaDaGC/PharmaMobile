package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import pe.edu.upeu.pharmamobil.data.remote.dto.ErrorResponseDto
import pe.edu.upeu.pharmamobil.domain.error.ErrorApi
import pe.edu.upeu.pharmamobil.domain.error.ErrorApiException

suspend fun <T> ejecutarLlamada(bloque: suspend () -> T): Result<T> {
    return try {
        Result.success(bloque())
    } catch (cancelacion: CancellationException) {
        // MUY IMPORTANTE: La cancelación de corrutinas NUNCA debe ser capturada como error
        throw cancelacion
    } catch (e: ClientRequestException) {
        val errorApi = traducirCliente(e)
        Result.failure(ErrorApiException(errorApi))
    } catch (e: ServerResponseException) {
        Result.failure(ErrorApiException(ErrorApi.Servidor, "Error en el servidor backend"))
    } catch (e: HttpRequestTimeoutException) {
        Result.failure(ErrorApiException(ErrorApi.TiempoAgotado, "Tiempo de espera agotado"))
    } catch (e: IOException) {
        Result.failure(ErrorApiException(ErrorApi.SinConexion, "Sin conexión a internet"))
    } catch (e: Exception) {
        // Captura cualquier otro fallo no previsto para evitar el crash
        Result.failure(e)
    }
}

private suspend fun traducirCliente(e: ClientRequestException): ErrorApi {
    return try {
        val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }
        val cuerpoTexto = runCatching { e.response.bodyAsText() }.getOrDefault("")

        val cuerpoDto = if (cuerpoTexto.isNotBlank()) {
            runCatching { jsonParser.decodeFromString<ErrorResponseDto>(cuerpoTexto) }.getOrNull()
        } else null

        when (e.response.status.value) {
            400 -> {
                val mensajeError = cuerpoDto?.validationErrors?.values?.firstOrNull()
                    ?: cuerpoDto?.message
                    ?: "El nombre de la categoría debe tener al menos 3 caracteres"

                ErrorApi.Validacion(
                    mensajeCustom = mensajeError,
                    errores = cuerpoDto?.validationErrors.orEmpty()
                )
            }
            404 -> ErrorApi.NoEncontrado
            409 -> {
                val mensajeError = cuerpoDto?.message
                    ?: "No se puede eliminar la categoría porque tiene productos asociados"
                ErrorApi.Conflicto(mensajeError)
            }
            else -> ErrorApi.Servidor
        }
    } catch (ex: Exception) {
        // En caso de fallo al procesar la respuesta, retorna una validación por defecto sin romper la app
        ErrorApi.Validacion(mensajeCustom = "Error de validación en los datos enviados")
    }
}