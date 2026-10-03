package pe.edu.upeu.pharmamobil.presentation.categoria

import pe.edu.upeu.pharmamobil.domain.model.Categoria

data class FormularioCategoria(
    val id: Long = 0L,
    val nombre: String = "",
    val descripcion: String = "",
    val nombreError: String? = null,
    val descripcionError: String? = null
)

data class CategoriaUiState(
    val fase: Fase = Fase.Cargando,
    val formulario: FormularioCategoria = FormularioCategoria(),
    val operacion: Operacion = Operacion.Inactiva,
    val mensajeExito: String? = null
)

sealed interface Fase {
    data object Cargando : Fase
    data object SinCategorias : Fase
    data class ConCategorias(val categorias: List<Categoria>) : Fase
    data class Error(val mensaje: String) : Fase
}

sealed interface Operacion {
    data object Inactiva : Operacion
    data class EnCurso(val tipo: Tipo) : Operacion
    data class Fallida(val mensaje: String) : Operacion
    enum class Tipo { Crear, Actualizar, Eliminar }
}