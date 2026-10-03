package pe.edu.upeu.pharmamobil.domain.usecase

import pe.edu.upeu.pharmamobil.domain.repository.CategoriaRepository

class EliminarCategoriaUseCase(private val repository: CategoriaRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> = repository.eliminar(id)
}