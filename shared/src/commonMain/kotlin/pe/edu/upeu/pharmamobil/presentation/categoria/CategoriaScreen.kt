package pe.edu.upeu.pharmamobil.presentation.categoria

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoriaScreen(
    viewModel: CategoriaViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val status = state) {
            is CategoriaUiState.Loading -> {
                CircularProgressIndicator()
            }
            is CategoriaUiState.Success -> {
                if (status.categorias.isEmpty()) {
                    Text("No hay categorías registradas en la base de datos.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(status.categorias) { categoria ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = categoria.nombre,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (categoria.descripcion.isNotBlank()) {
                                        Text(
                                            text = categoria.descripcion,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is CategoriaUiState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error al conectar: ${status.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.cargarCategorias() }) {
                        Text("Reintentar")
                    }
                }
            }
        }
    }
}