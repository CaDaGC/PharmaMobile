package pe.edu.upeu.pharmamobil.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pe.edu.upeu.pharmamobil.domain.model.Categoria

// DTO para recibir la respuesta del backend (CategoriaResponseDTO)
@Serializable
data class CategoriaResponseDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("estado") val estado: Boolean? = true,
    @SerialName("fechaCreacion") val fechaCreacion: String? = null,
    @SerialName("fechaModificacion") val fechaModificacion: String? = null
)

// DTO para enviar peticiones al backend (CategoriaRequestDTO)
@Serializable
data class CategoriaRequestDto(
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("estado") val estado: Boolean = true
)

// Mapeador de DTO a Modelo de Dominio
fun CategoriaResponseDto.toDomain(): Categoria {
    return Categoria(
        id = this.id ?: 0L,
        nombre = this.nombre,
        descripcion = this.descripcion ?: "",
        estado = this.estado ?: true
    )
}

// Mapeador de Modelo de Dominio a DTO de Petición
fun Categoria.toRequestDto(): CategoriaRequestDto {
    return CategoriaRequestDto(
        nombre = this.nombre,
        descripcion = this.descripcion,
        estado = this.estado
    )
}
