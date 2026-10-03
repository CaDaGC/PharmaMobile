package pe.edu.upeu.pharmamobil.domain.usecase

import pe.edu.upeu.pharmamobil.domain.model.Categoria
import pe.edu.upeu.pharmamobil.domain.repository.CategoriaRepository

class ActualizarCategoriaUseCase(private val repository: CategoriaRepository) {
    suspend operator fun invoke(categoria: Categoria): Result<Categoria> = repository.actualizar(categoria)
}