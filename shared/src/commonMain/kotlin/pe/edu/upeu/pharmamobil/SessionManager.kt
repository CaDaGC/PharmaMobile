package pe.edu.upeu.pharmamobil

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {
    private val _isSessionValid = MutableStateFlow(true)
    val isSessionValid = _isSessionValid.asStateFlow()

    fun invalidateSession() {
        _isSessionValid.value = false
    }

    fun resetSession() {
        _isSessionValid.value = true
    }
}