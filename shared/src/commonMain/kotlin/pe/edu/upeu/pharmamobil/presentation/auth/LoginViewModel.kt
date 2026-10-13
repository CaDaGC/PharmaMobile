package pe.edu.upeu.pharmamobil.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository

data class LoginUiState(
    val usuario: String = "",
    val clave: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val sesionExitosas: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(LoginUiState())
        private set

    fun onUsuarioChanged(valor: String) {
        state = state.copy(usuario = valor, error = null)
    }

    fun onClaveChanged(valor: String) {
        state = state.copy(clave = valor, error = null)
    }

    fun onIniciarSesion() {
        if (state.usuario.isBlank() || state.clave.isBlank()) {
            state = state.copy(error = "Por favor ingrese usuario y contraseña")
            return
        }

        viewModelScope.launch {
            state = state.copy(cargando = true, error = null)
            try {
                val credenciales = Credenciales(usuario = state.usuario, clave = state.clave)
                authRepository.iniciarSesion(credenciales)
                state = state.copy(cargando = false, sesionExitosas = true)
            } catch (e: Exception) {
                state = state.copy(
                    cargando = false,
                    error = "Error al iniciar sesión: Verifique sus credenciales o conexión."
                )
            }
        }
    }
}