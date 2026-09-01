package pe.edu.upeu.pharmamobil.presentation.producto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.pharmamobile.data.MockData
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.presentation.components.ValidatedTextField
import pe.edu.upeu.pharmamobile.presentation.producto.ProductoValidator

@Composable
fun ProductoScreen(
    onRegistrar: (Producto) -> Unit = {}
) {
    // Estado para las pestañas de inventario solicitadas en la guía autónoma
    var tabSeleccionada by remember { mutableStateOf(0) }
    val titulosTabs = listOf("Activos", "Inactivos", "Bajo Stock")

    // Estados para el formulario de registro existente
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var precioError by remember { mutableStateOf<String?>(null) }
    var stockError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    fun validar(): Producto? {
        nombreError = ProductoValidator.validarNombre(nombre)
        precioError = ProductoValidator.validarPrecio(precio)
        stockError = ProductoValidator.validarStock(stock)

        if (nombreError != null || precioError != null || stockError != null) return null

        return Producto(
            id = 0L,
            nombre = nombre.trim(),
            precio = precio.toDouble(),
            stock = stock.toInt(),
            activo = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("PharmaMobil", style = MaterialTheme.typography.titleLarge)
        Text("Gestión de Inventario y Productos", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(4.dp))

        // Selector de Pestañas (Tabs)[cite: 2]
        PrimaryTabRow(selectedTabIndex = tabSeleccionada) {
            titulosTabs.forEachIndexed { index, titulo ->
                Tab(
                    selected = tabSeleccionada == index,
                    onClick = { tabSeleccionada = index },
                    text = { Text(titulo) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Formulario de Registro compacto o visible
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Registro de Producto", style = MaterialTheme.typography.titleMedium)

                ValidatedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = "Nombre",
                    error = nombreError,
                    modifier = Modifier.fillMaxWidth()
                )

                ValidatedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = "Precio",
                    error = precioError,
                    modifier = Modifier.fillMaxWidth()
                )

                ValidatedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = "Stock",
                    error = stockError,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        mensajeExito = null
                        val producto = validar()
                        if (producto != null) {
                            onRegistrar(producto)
                            mensajeExito = "Producto \"${producto.nombre}\" registrado correctamente"
                            nombre = ""
                            precio = ""
                            stock = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar")
                }

                mensajeExito?.let {
                    Text(it, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text("Listado de Inventario", style = MaterialTheme.typography.titleMedium)

        // Filtrado estricto según la pestaña seleccionada[cite: 2]
        val productosFiltrados = when (tabSeleccionada) {
            0 -> MockData.listaProductos.filter { it.activo && !it.esBajoStock }
            1 -> MockData.listaProductos.filter { !it.activo }
            else -> MockData.listaProductos.filter { it.esBajoStock } // stock <= 5[cite: 2]
        }

        // Listado dinámico en LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(productosFiltrados) { producto ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = producto.nombre, style = MaterialTheme.typography.titleSmall)
                        Text(text = "Precio: S/. ${producto.precio}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Stock: ${producto.stock}", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = if (producto.activo) "Estado: Activo" else "Estado: Inactivo",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (producto.activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}