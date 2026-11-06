package pe.edu.upeu.pharmamobil.presentation.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.data.repository.CategoriaRepositoryImpl
import pe.edu.upeu.pharmamobil.domain.model.Categoria
import pe.edu.upeu.pharmamobil.domain.error.ErrorApi
import pe.edu.upeu.pharmamobil.domain.error.ErrorApiException

class CategoriaViewModel(
    private val repository: CategoriaRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState: StateFlow<CategoriaUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoriaUiState()
    )

    init {
        observarCategorias()
    }

    private fun observarCategorias() {
        viewModelScope.launch {
            repository.observar().collect { categorias ->
                _uiState.update { current ->
                    current.copy(
                        categorias = categorias,
                        ultimaSincronizacion = repository.obtenerTextoAntiguedad(),
                        pendientesCount = categorias.count { it.pendiente }
                    )
                }
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeError = null, mensajeExito = null, campoError = null) }
    }

    fun prepararEdicion(categoria: Categoria) {
        _uiState.update { it.copy(categoriaEditandoId = categoria.id) }
        limpiarMensajes()
    }

    fun cancelarEdicion() {
        _uiState.update { it.copy(categoriaEditandoId = null) }
        limpiarMensajes()
    }

    fun guardarCategoria(nombre: String, descripcion: String, onSuccessCallback: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }
            limpiarMensajes()
            try {
                val idActual = _uiState.value.categoriaEditandoId
                if (idActual == null) {
                    val nueva = Categoria(nombre = nombre, descripcion = descripcion, estado = true)
                    repository.registrar(nueva)
                    _uiState.update { it.copy(mensajeExito = "Categoría guardada localmente/sincronizada") }
                } else {
                    val editada = Categoria(id = idActual, nombre = nombre, descripcion = descripcion, estado = true)
                    repository.actualizar(editada).getOrThrow()
                    _uiState.update { it.copy(mensajeExito = "Categoría actualizada correctamente") }
                }
                _uiState.update { it.copy(categoriaEditandoId = null) }
                onSuccessCallback()
            } catch (e: ErrorApiException) {
                val validacion = e.errorApi as? ErrorApi.Validacion
                _uiState.update {
                    it.copy(
                        mensajeError = e.message ?: "Error al procesar la solicitud",
                        campoError = validacion?.errores?.keys?.firstOrNull()
                    )
                }
            } catch (e: IllegalArgumentException) {
                _uiState.update { it.copy(mensajeError = e.message, campoError = "nombre") }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = e.message ?: "Error desconocido") }
            } finally {
                _uiState.update { it.copy(estaCargando = false) }
            }
        }
    }

    fun eliminarCategoria(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }
            limpiarMensajes()
            try {
                repository.eliminar(id).getOrThrow()
                _uiState.update { it.copy(mensajeExito = "Categoría eliminada correctamente") }
            } catch (e: ErrorApiException) {
                _uiState.update { it.copy(mensajeError = e.message ?: "Error al eliminar") }
            } catch (e: IllegalArgumentException) {
                _uiState.update { it.copy(mensajeError = e.message, campoError = "nombre") }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = e.message ?: "Error desconocido") }
            } finally {
                _uiState.update { it.copy(estaCargando = false) }
            }
        }
    }

    fun sincronizarManual() {
        viewModelScope.launch {
            _uiState.update { it.copy(enCursoSync = true, mensajeError = null, mensajeExito = null, campoError = null) }
            try {
                repository.sincronizar()
                _uiState.update { it.copy(mensajeExito = "Sincronización completada con éxito") }
            } catch (e: Exception) {
                _uiState.update { it.copy(mensajeError = "Error al sincronizar: ${e.message}") }
            } finally {
                _uiState.update { it.copy(enCursoSync = false) }
            }
        }
    }
}