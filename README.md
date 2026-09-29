# PharmaMobil - Módulo de Categorías (Ktor Client REST)

## Configuración de Conexión
- **URL Base (Emulador Android):** `http://10.0.2.2:8080/`
- **Endpoint consumido:** `/api/v1/categorias`
- **Método HTTP:** `GET`

## Estructura del DTO (CategoriaResponseDto)
```kotlin
@Serializable
data class CategoriaResponseDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("nombre") val nombre: String,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("estado") val estado: Boolean? = true,
    @SerialName("fechaCreacion") val fechaCreacion: String? = null,
    @SerialName("fechaModificacion") val fechaModificacion: String? = null
)