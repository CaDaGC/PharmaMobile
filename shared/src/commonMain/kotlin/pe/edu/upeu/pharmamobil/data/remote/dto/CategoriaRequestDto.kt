package pe.edu.upeu.pharmamobil.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoriaRequestDto(
    val nombre: String,
    val descripcion: String? = null,
    val estado: Boolean = true
)

@Serializable
data class ErrorResponseDto(
    val message: String? = null,
    val validationErrors: Map<String, String> = emptyMap()
)