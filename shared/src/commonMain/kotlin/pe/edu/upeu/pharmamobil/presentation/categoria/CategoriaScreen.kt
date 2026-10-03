package pe.edu.upeu.pharmamobil.presentation.categoria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CategoriaScreen(
    viewModel: CategoriaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var nombreState by remember { mutableStateOf("") }
    var descripcionState by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Gestión de Categorías",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Formulario de ingreso / edición
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (uiState.categoriaEditandoId == null) "Nueva Categoría" else "Editar Categoría",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nombreState,
                    onValueChange = { nombreState = it },
                    label = { Text("Nombre") },
                    isError = uiState.mensajeError != null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcionState,
                    onValueChange = { descripcionState = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mensaje de error (HTTP 400 u otros) mostrado en rojo debajo del formulario
                uiState.mensajeError?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (uiState.categoriaEditandoId != null) {
                        OutlinedButton(
                            onClick = {
                                viewModel.cancelarEdicion()
                                nombreState = ""
                                descripcionState = ""
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Cancelar")
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.guardarCategoria(
                                nombre = nombreState,
                                descripcion = descripcionState
                            )
                        },
                        enabled = !uiState.estaCargando
                    ) {
                        Text(if (uiState.categoriaEditandoId == null) "Guardar" else "Actualizar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Indicador de Carga
        if (uiState.estaCargando) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Listado de Categorías
        Text(
            text = "Listado de Categorías",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.categorias) { categoria ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = categoria.nombre,
                                style = MaterialTheme.typography.titleSmall
                            )
                            if (categoria.descripcion.isNotBlank()) {
                                Text(
                                    text = categoria.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }

                        Row {
                            IconButton(onClick = {
                                viewModel.prepararEdicion(categoria)
                                nombreState = categoria.nombre
                                descripcionState = categoria.descripcion
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }

                            IconButton(onClick = {
                                viewModel.eliminarCategoria(categoria.id)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}