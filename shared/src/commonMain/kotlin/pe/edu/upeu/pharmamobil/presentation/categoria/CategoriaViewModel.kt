package pe.edu.upeu.pharmamobil.presentation.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.model.Categoria
import pe.edu.upeu.pharmamobil.domain.usecase.ActualizarCategoriaUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.EliminarCategoriaUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.ListarCategoriasUseCase
import pe.edu.upeu.pharmamobil.domain.usecase.RegistrarCategoriaUseCase

class CategoriaViewModel(
    private val listarCategoriasUseCase: ListarCategoriasUseCase,
    private val registrarCategoriaUseCase: RegistrarCategoriaUseCase,
    private val actualizarCategoriaUseCase: ActualizarCategoriaUseCase,
    private val eliminarCategoriaUseCase: EliminarCategoriaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState: StateFlow<CategoriaUiState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, mensajeError = null) }
            try {
                listarCategoriasUseCase()
                    .onSuccess { lista ->
                        _uiState.update { it.copy(categorias = lista, estaCargando = false) }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(estaCargando = false, mensajeError = error.message ?: "Error al cargar categorías")
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(estaCargando = false, mensajeError = e.message ?: "Error inesperado")
                }
            }
        }
    }

    fun guardarCategoria(nombre: String, descripcion: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, mensajeError = null) }
            try {
                val categoria = Categoria(
                    id = _uiState.value.categoriaEditandoId ?: 0,
                    nombre = nombre,
                    descripcion = descripcion
                )

                val resultado = if (_uiState.value.categoriaEditandoId == null) {
                    registrarCategoriaUseCase(categoria)
                } else {
                    actualizarCategoriaUseCase(categoria)
                }

                resultado.fold(
                    onSuccess = {
                        _uiState.update { it.copy(categoriaEditandoId = null) }
                        cargarCategorias()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                estaCargando = false,
                                mensajeError = error.message ?: "Error de validación al guardar"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        estaCargando = false,
                        mensajeError = e.message ?: "Ocurrió un error inesperado al guardar"
                    )
                }
            }
        }
    }

    fun prepararEdicion(categoria: Categoria) {
        _uiState.update {
            it.copy(categoriaEditandoId = categoria.id, mensajeError = null)
        }
    }

    fun cancelarEdicion() {
        _uiState.update {
            it.copy(categoriaEditandoId = null, mensajeError = null)
        }
    }

    fun eliminarCategoria(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, mensajeError = null) }
            try {
                eliminarCategoriaUseCase(id)
                    .onSuccess {
                        cargarCategorias()
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(estaCargando = false, mensajeError = error.message ?: "Error al eliminar")
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(estaCargando = false, mensajeError = e.message ?: "Error al eliminar la categoría")
                }
            }
        }
    }
}