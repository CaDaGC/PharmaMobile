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
            listarCategoriasUseCase()
                .onSuccess { lista ->
                    _uiState.update { it.copy(categorias = lista, estaCargando = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(estaCargando = false, mensajeError = error.message ?: "Error al cargar categorías")
                    }
                }
        }
    }

    fun guardarCategoria(nombre: String, descripcion: String, onSuccessCallback: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true, mensajeError = null, mensajeExito = null) }
            try {
                val esEdicion = _uiState.value.categoriaEditandoId != null
                val categoria = Categoria(
                    id = _uiState.value.categoriaEditandoId ?: 0L,
                    nombre = nombre.trim(),
                    descripcion = descripcion.trim()
                )

                val resultado = if (!esEdicion) {
                    registrarCategoriaUseCase(categoria)
                } else {
                    actualizarCategoriaUseCase(categoria)
                }

                resultado.fold(
                    onSuccess = {
                        val mensaje = if (!esEdicion) "Categoría registrada correctamente" else "Categoría actualizada correctamente"
                        _uiState.update {
                            it.copy(
                                categoriaEditandoId = null,
                                estaCargando = false,
                                mensajeExito = mensaje,
                                mensajeError = null
                            )
                        }
                        onSuccessCallback()
                        cargarCategorias()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                estaCargando = false,
                                mensajeError = error.message ?: "El nombre debe tener al menos 3 caracteres",
                                mensajeExito = null
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        estaCargando = false,
                        mensajeError = e.message ?: "Ocurrió un error inesperado",
                        mensajeExito = null
                    )
                }
            }
        }
    }

    fun prepararEdicion(categoria: Categoria) {
        _uiState.update {
            it.copy(categoriaEditandoId = categoria.id, mensajeError = null, mensajeExito = null)
        }
    }

    fun cancelarEdicion() {
        _uiState.update {
            it.copy(categoriaEditandoId = null, mensajeError = null, mensajeExito = null)
        }
    }

    fun limpiarMensajes() {
        _uiState.update {
            it.copy(mensajeError = null, mensajeExito = null)
        }
    }

    fun eliminarCategoria(id: Long) {
        viewModelScope.launch {
            // 1. Reiniciamos ambos mensajes antes de la llamada
            _uiState.update {
                it.copy(
                    estaCargando = true,
                    mensajeError = null,
                    mensajeExito = null
                )
            }

            val resultado = eliminarCategoriaUseCase(id)

            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            estaCargando = false,
                            mensajeExito = "Categoría eliminada correctamente",
                            mensajeError = null
                        )
                    }
                    cargarCategorias()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            estaCargando = false,
                            mensajeExito = null, // CLAVE: Desaparece el texto verde inmediatamente
                            mensajeError = error.message ?: "No se puede eliminar la categoría porque tiene productos asociados"
                        )
                    }
                }
            )
        }
    }
}