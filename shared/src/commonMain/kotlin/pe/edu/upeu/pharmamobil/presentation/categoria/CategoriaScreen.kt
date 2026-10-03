package pe.edu.upeu.pharmamobil.presentation.categoria

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoriaScreen(
    viewModel: CategoriaViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var nombreInput by remember { mutableStateOf("") }
    var descripcionInput by remember { mutableStateOf("") }
    var categoriaEditandoId by remember { mutableStateOf(0L) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Formulario de Registro / Edición
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (categoriaEditandoId == 0L) "Nueva Categoría" else "Editar Categoría",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = nombreInput,
                    onValueChange = { nombreInput = it },
                    label = { Text("Nombre") },
                    isError = state.formulario.nombreError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                state.formulario.nombreError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcionInput,
                    onValueChange = { descripcionInput = it },
                    label = { Text("Descripción") },
                    isError = state.formulario.descripcionError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                state.formulario.descripcionError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.guardarCategoria(nombreInput, descripcionInput, categoriaEditandoId)
                        nombreInput = ""
                        descripcionInput = ""
                        categoriaEditandoId = 0L
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = state.operacion !is Operacion.EnCurso
                ) {
                    Text(if (categoriaEditandoId == 0L) "Guardar" else "Actualizar")
                }
            }
        }

        // Evaluación exhaustiva de las Fases de la Pantalla
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val fase = state.fase) {
                is Fase.Cargando -> CircularProgressIndicator()
                is Fase.SinCategorias -> Text("Aún no hay categorías registradas.")
                is Fase.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: ${fase.mensaje}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.cargarCategorias() }) { Text("Reintentar") }
                }
                is Fase.ConCategorias -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(fase.categorias) { categoria ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = categoria.nombre, style = MaterialTheme.typography.titleMedium)
                                        if (categoria.descripcion.isNotBlank()) {
                                            Text(text = categoria.descripcion, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            categoriaEditandoId = categoria.id
                                            nombreInput = categoria.nombre
                                            descripcionInput = categoria.descripcion
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        }
                                        IconButton(onClick = { viewModel.eliminar(categoria.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}