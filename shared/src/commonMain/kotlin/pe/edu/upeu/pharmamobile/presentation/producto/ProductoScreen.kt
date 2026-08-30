package pe.edu.upeu.pharmamobil.presentation.producto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 1. Definición del Modelo de Datos del Producto
data class Producto(
    val nombre: String,
    val precio: Double,
    val stock: Int
)

@Composable
fun ProductoScreen() {

    // 2. Declaración de Estados de Entrada
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    // 3. Control de Intento de Registro (Evita mostrar errores antes de presionar "Registrar")
    var intentoRegistrar by remember { mutableStateOf(false) }

    // 4. Estados de Mensajes y Objeto Guardado
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var productoRegistrado by remember { mutableStateOf<Producto?>(null) }

    // 5. Lógica de Conversión Segura y Validación Regla por Regla
    val nombreValido = nombre.isNotBlank()

    val precioDouble = precio.toDoubleOrNull()
    val precioValido = precioDouble != null && precioDouble > 0.0

    val stockInt = stock.toIntOrNull()
    val stockValido = stockInt != null && stockInt >= 0

    // Determinación de Mensajes de Error Específicos
    val nombreError = when {
        !intentoRegistrar -> null
        nombre.isBlank() -> "El nombre es obligatorio."
        else -> null
    }

    val precioError = when {
        !intentoRegistrar -> null
        precio.isBlank() -> "Ingrese un precio numérico."
        precioDouble == null -> "Ingrese un precio numérico."
        precioDouble <= 0.0 -> "El precio debe ser mayor que cero."
        else -> null
    }

    val stockError = when {
        !intentoRegistrar -> null
        stock.isBlank() -> "Ingrese un stock entero."
        stockInt == null -> "Ingrese un stock entero."
        stockInt < 0 -> "El stock no puede ser negativo."
        else -> null
    }

    // 6. UI Declarativa en Compose
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "PharmaMobil",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Registro de Producto",
            style = MaterialTheme.typography.titleMedium
        )

        // Campo Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            isError = nombreError != null,
            supportingText = {
                nombreError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Precio
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            isError = precioError != null,
            supportingText = {
                precioError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Stock
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            isError = stockError != null,
            supportingText = {
                stockError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón Registrar
        Button(
            onClick = {
                intentoRegistrar = true
                mensajeExito = null

                // Se valida que todos los criterios sean exitosos
                if (nombreValido && precioValido && stockValido && precioDouble != null && stockInt != null) {

                    // Instanciación del Objeto Producto
                    val nuevoProducto = Producto(
                        nombre = nombre.trim(),
                        precio = precioDouble,
                        stock = stockInt
                    )

                    productoRegistrado = nuevoProducto
                    mensajeExito = "¡Éxito! Producto \"${nuevoProducto.nombre}\" (S/ ${nuevoProducto.precio}, Stock: ${nuevoProducto.stock}) registrado correctamente."

                    // Limpieza del Formulario
                    nombre = ""
                    precio = ""
                    stock = ""
                    intentoRegistrar = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        // Retroalimentación de Éxito
        mensajeExito?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}