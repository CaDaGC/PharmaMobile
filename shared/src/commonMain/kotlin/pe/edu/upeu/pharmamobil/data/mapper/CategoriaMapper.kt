package pe.edu.upeu.pharmamobil.data.mapper

import pe.edu.upeu.pharmamobil.data.remote.dto.CategoriaRequestDto
import pe.edu.upeu.pharmamobil.data.remote.dto.CategoriaResponseDto
import pe.edu.upeu.pharmamobil.domain.model.Categoria

fun CategoriaResponseDto.toDomain(): Categoria {
    return Categoria(
        id = id ?: 0L,
        nombre = nombre,
        descripcion = descripcion.orEmpty()
    )
}

fun Categoria.toRequest(): CategoriaRequestDto {
    return CategoriaRequestDto(
        nombre = nombre,
        descripcion = descripcion.ifBlank { null },
        estado = true
    )
}