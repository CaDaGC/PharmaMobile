package pe.edu.upeu.pharmamobil.domain.error

sealed interface ErrorApi {
    data class Validacion(
        val mensajeCustom: String = "Error de validación",
        val errores: Map<String, String> = emptyMap()
    ) : ErrorApi

    object NoEncontrado : ErrorApi
    data class Conflicto(val mensaje: String) : ErrorApi
    object Servidor : ErrorApi
    object TiempoAgotado : ErrorApi
    object SinConexion : ErrorApi
}

class ErrorApiException(
    val errorApi: ErrorApi,
    mensajeDetalle: String? = null
) : Exception(
    when (errorApi) {
        is ErrorApi.Validacion -> errorApi.mensajeCustom
        is ErrorApi.Conflicto -> errorApi.mensaje
        ErrorApi.NoEncontrado -> "Recurso no encontrado"
        ErrorApi.Servidor -> mensajeDetalle ?: "Error interno del servidor"
        ErrorApi.TiempoAgotado -> "Tiempo de espera agotado"
        ErrorApi.SinConexion -> "Sin conexión a internet"
    }
)