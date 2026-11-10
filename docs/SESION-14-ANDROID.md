# Sesión 14 · PharmaMobil Android · Equipo 3

Base de integración: feature/integrador-13 (e828d37). Rama de preparación: feature/builds-14.

## Alcance

El recorrido integrado usa autenticación y el CRUD de categorías con PharmaSoft, permisos por rol y sincronización de categorías pendientes. Productos y clientes usan repositorios en memoria en la app. Se congela el alcance para la entrega. La ficha formal de la sesión 13 queda pendiente de contrastar con el documento original.

## Matriz

| Comprobación | Xiaomi Redmi Note (modelo y Android por confirmar) |
|---|---|
| Instalación limpia y apertura del APK firmado | Pendiente |
| Login válido e inválido | Pendiente |
| CRUD de categorías y error de validación | Pendiente |
| Operación rechazada por rol | Pendiente |
| Modo avión: abrir, leer y crear | Pendiente |
| Reconexión y sincronización sin duplicados | Pendiente |
| Permiso de notificaciones: conceder, denegar y denegación persistente | Pendiente |
| Cerrar sesión y reabrir | Pendiente |

Por el momento hay un solo teléfono disponible. La validación física completa queda pendiente de acceso a PharmaSoft desde el dispositivo.

Para cada defecto registrar ID, dispositivo y Android, pasos numerados, resultado esperado, resultado observado, evidencia, severidad y estado.

## Checklist

- [ ] Verificar APK y AAB de la misma versión y registrar titular, huella SHA-256 y caducidad.
- [ ] Guardar copia de respaldo del almacén y custodiar las credenciales por separado.
- [ ] Probar el APK minificado con backend y cuentas de prueba.
- [ ] Completar la matriz y adjuntar evidencia de la prueba física.
- [ ] Confirmar que un tercero puede seguir la guía de instalación.
- [ ] Etiquetar v1.0-final en el commit exacto usado para los artefactos definitivos.
