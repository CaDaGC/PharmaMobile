# PharmaMobil - Aplicación Móvil Multiplataforma (KMP)

## Entrega Android de la sesión 14

Equipo 3 · versión 1.0.0 (código 1) · pe.edu.upeu.pharmamobil.

Para firmar, crea keystore.properties en la raíz del proyecto. Define storeFile con la ruta del almacén (usa barras / en Windows), storePassword, keyAlias y keyPassword. Guarda el almacén y una copia de respaldo fuera del repositorio. El archivo local está excluido de Git. Debug compila sin él.

En PowerShell, con el JDK configurado, ejecuta:

    .\gradlew.bat :androidApp:bundleRelease :androidApp:assembleRelease

Por defecto la app busca PharmaSoft en el equipo anfitrión del emulador Android. Para un teléfono físico, agrega -PpharmaBackendUrl=https://TU-HOST al comando con el origen que responda desde el teléfono. Gradle incorpora esa URL al artefacto. Comprueba /api/health y usa una base de datos de prueba.

Los resultados quedan en androidApp/build/outputs/bundle/release y androidApp/build/outputs/apk/release. Comprueba la firma del APK con apksigner verify --print-certs. Instala el APK transfiriéndolo al teléfono y abriéndolo desde Archivos, o usa adb install si el equipo está conectado a un ordenador con ADB. El AAB se entrega como paquete de distribución.

Consulta [la matriz y el checklist](docs/SESION-14-ANDROID.md).

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


--------------------------------------------------------------------------------------
### 5. Código específico de plataforma

PharmaMobil utiliza el mecanismo `expect` / `actual` e inyección de dependencias para aislar las capacidades nativas del sistema operativo:

| Capacidad | Tipo / Firma | Archivo expect | Implementation Android | Implementation iOS |
| :--- | :--- | :--- | :--- | :--- |
| **Formato de Moneda** | `expect fun formatearSoles(valor: Double): String` | `commonMain/.../platform/Formato.kt` | `androidMain/.../platform/Formato.android.kt` (`NumberFormat`) | `iosMain/.../platform/Formato.ios.kt` (`NSNumberFormatter`) |
| **Compartir Producto** | `interface Compartidor` | `commonMain/.../domain/platform/Compartidor.kt` | `androidMain/.../platform/CompartidorAndroid.kt` (`Intent.ACTION_SEND`) | `iosMain/.../platform/CompartidorIos.kt` (`UIActivityViewController`) |
| **Información Dispositivo** | `expect class InfoDispositivo()` | `commonMain/.../platform/InfoDispositivo.kt` | `androidMain/.../platform/InfoDispositivo.android.kt` (`Build.VERSION`) | `iosMain/.../platform/InfoDispositivo.ios.kt` (`UIDevice.currentDevice`) |
| **Módulo de Inyección** | `expect val platformModule: Module` | `commonMain/.../di/PlatformModule.kt` | `androidMain/.../di/PlatformModule.android.kt` | `iosMain/.../di/PlatformModule.ios.kt` |