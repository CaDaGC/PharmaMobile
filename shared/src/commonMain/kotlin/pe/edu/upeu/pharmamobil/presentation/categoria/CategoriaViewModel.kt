package pe.edu.upeu.pharmamobil.presentation.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.error.ErrorApi
import pe.edu.upeu.pharmamobil.domain.error.ErrorApiException
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
        _uiState.update { it.copy(fase = Fase.Cargando) }
        viewModelScope.launch {
            listarCategoriasUseCase()
                .onSuccess { lista ->
                    _uiState.update {
                        it.copy(
                            fase = if (lista.isEmpty()) Fase.SinCategorias else Fase.ConCategorias(lista)
                        )
                    }
                }
                .onFailure { fallo ->
                    _uiState.update { it.copy(fase = Fase.Error(mensajeDe(fallo))) }
                }
        }
    }

    fun guardarCategoria(nombre: String, descripcion: String, id: Long = 0L) {
        val tipoOperacion = if (id == 0L) Operacion.Tipo.Crear else Operacion.Tipo.Actualizar
        _uiState.update { it.copy(operacion = Operacion.EnCurso(tipoOperacion)) }

        viewModelScope.launch {
            val categoria = Categoria(id = id, nombre = nombre, descripcion = descripcion)
            val resultado = if (id == 0L) registrarCategoriaUseCase(categoria) else actualizarCategoriaUseCase(categoria)

            resultado.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        operacion = Operacion.Inactiva,
                        formulario = FormularioCategoria(),
                        mensajeExito = if (id == 0L) "Categoría registrada" else "Categoría actualizada"
                    )
                }
                cargarCategorias()
            }.onFailure { fallo ->
                manejarFallo(fallo)
            }
        }
    }

    fun eliminar(id: Long) {
        _uiState.update { it.copy(operacion = Operacion.EnCurso(Operacion.Tipo.Eliminar)) }
        viewModelScope.launch {
            eliminarCategoriaUseCase(id)
                .onSuccess {
                    _uiState.update {
                        it.copy(operacion = Operacion.Inactiva, mensajeExito = "Categoría eliminada")
                    }
                    cargarCategorias()
                }
                .onFailure { fallo -> manejarFallo(fallo) }
        }
    }

    private fun manejarFallo(fallo: Throwable) {
        val error = (fallo as? ErrorApiException)?.error
        when (error) {
            is ErrorApi.Validacion -> {
                _uiState.update {
                    it.copy(
                        operacion = Operacion.Inactiva,
                        formulario = it.formulario.copy(
                            nombreError = error.porCampo["nombre"],
                            descripcionError = error.porCampo["descripcion"]
                        )
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(operacion = Operacion.Fallida(mensajeDe(fallo)))
                }
            }
        }
    }

    private fun mensajeDe(fallo: Throwable): String {
        return when (val error = (fallo as? ErrorApiException)?.error) {
            is ErrorApi.SinConexion -> "Sin conexión a la red."
            is ErrorApi.TiempoAgotado -> "Tiempo de espera agotado."
            is ErrorApi.NoEncontrado -> "Recurso no encontrado."
            is ErrorApi.Servidor -> "Error interno del servidor."
            is ErrorApi.Conflicto -> error.mensaje
            else -> fallo.message ?: "Error desconocido"
        }
    }
}