# PharmaMobil - Aplicación Móvil Multiplataforma (KMP)

## Módulo de Categorías - Conectividad REST

### 1. Configuración de Conexión HTTP
- **API Backend:** Spring Boot 4.0.7 con Oracle DB
- **URL Base (Emulador Android):** `http://10.0.2.2:8080/api/v1`
- **URL Base (Simulador iOS / Desktop):** `http://localhost:8080/api/v1`
- **Cliente HTTP:** Ktor Client 3.0+
- **Motor de Red (Engine):** OkHttp (Android), Darwin (iOS)

---

### 2. Catálogo de Endpoints de Categorías
| Método | Ruta | Parámetros | Respuesta esperada | Códigos de error |
|---|---|---|---|---|
| **GET** | `/categorias` | `limit`, `offset` (query) | 200 OK (Lista de categorías) | 500 |
| **GET** | `/categorias/{id}` | `id` (Path) | 200 OK (Objeto categoría) | 400, 404 |
| **POST** | `/categorias` | Body JSON | 201 Created (Categoría creada) | 400, 401 |
| **PUT** | `/categorias/{id}` | `id` (Path) + Body JSON | 200 OK (Categoría actualizada) | 400, 404 |
| **DELETE**| `/categorias/{id}` | `id` (Path) | 200 OK / 204 No Content | 401, 404 |

---

### 3. Estructura del DTO (`CategoriaResponseDto`)
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
---

### 4. Manejo de Errores y Excepciones (Módulo Categorías)
La aplicación mapea las respuestas del backend mediante la sealed interface ErrorApi:
- *Validación (HTTP 400):* Se capturan los mensajes específicos devueltos por Spring Boot para nombres vacíos o cortos (<3 caracteres) y se exponen en la UI sin cerrar la app.
- *Recurso no encontrado (HTTP 404):* Mapeado a ErrorApi.NoEncontrado cuando se intenta acceder o eliminar una categoría inexistente.
- *Conflicto (HTTP 409):* Mapeado a ErrorApi.Conflicto cuando se intenta eliminar una categoría con productos asociados.
- *Fallos de red y timeout:* Atrapados mediante IOException y HttpRequestTimeoutException, representados con ErrorApi.SinConexion y ErrorApi.TiempoAgotado.