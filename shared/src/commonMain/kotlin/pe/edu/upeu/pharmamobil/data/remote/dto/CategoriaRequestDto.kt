package pe.edu.upeu.pharmamobil.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoriaRequestDto(
    @SerialName("nombre")
    val nombre: String,

    @SerialName("descripcion")
    val descripcion: String? = null,

    @SerialName("estado")
    val estado: Boolean = true
)

@Serializable
data class ErrorResponseDto(
    @SerialName("message")
    val message: String? = null,

    @SerialName("validationErrors")
    val validationErrors: Map<String, String> = emptyMap()
)