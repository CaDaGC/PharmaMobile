package pe.edu.upeu.pharmamobil.presentation.categoria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.model.Categoria
import pe.edu.upeu.pharmamobil.domain.repository.CategoriaRepository

sealed interface CategoriaUiState {
    data object Loading : CategoriaUiState
    data class Success(val categorias: List<Categoria>) : CategoriaUiState
    data class Error(val message: String) : CategoriaUiState
}

class CategoriaViewModel(
    private val repository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoriaUiState>(CategoriaUiState.Loading)
    val uiState: StateFlow<CategoriaUiState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            _uiState.value = CategoriaUiState.Loading
            repository.getCategorias()
                .onSuccess { lista ->
                    _uiState.value = CategoriaUiState.Success(lista)
                }
                .onFailure { error ->
                    _uiState.value = CategoriaUiState.Error(error.message ?: "Error al conectar con la API")
                }
        }
    }
}