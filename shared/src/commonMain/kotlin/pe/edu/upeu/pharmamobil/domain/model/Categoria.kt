package pe.edu.upeu.pharmamobil.domain.model

data class Categoria(
    val id: Long = 0L,
    val nombre: String,
    val descripcion: String = "",
    val estado: Boolean = true
) {
    init {
        require(nombre.isNotBlank()) {
            "El nombre de la categoría no puede estar vacío"
        }
        require(nombre.length in 3..50) {
            "El nombre debe tener entre 3 y 50 caracteres"
        }
    }
}