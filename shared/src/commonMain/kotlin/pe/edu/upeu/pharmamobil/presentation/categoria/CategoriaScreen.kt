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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.permiso.EstadoPermiso
import pe.edu.upeu.pharmamobil.domain.permiso.GestorPermisos
import pe.edu.upeu.pharmamobil.domain.permiso.Permiso

@Composable
fun CategoriaScreen(
    viewModel: CategoriaViewModel,
    gestorPermisos: GestorPermisos? = null
) {
    val scope = rememberCoroutineScope()
    var estadoNotificaciones by remember { mutableStateOf<EstadoPermiso?>(null) }
    var solicitandoPermiso by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    var nombreState by remember { mutableStateOf("") }
    var descripcionState by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gestión de Categorías",
                style = MaterialTheme.typography.headlineMedium
            )
            // Botón para Sincronización Manual (Paso 3 y 4)
            IconButton(
                onClick = { viewModel.sincronizarManual() },
                enabled = !uiState.enCursoSync && !uiState.estaCargando
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Sincronizar Manual")
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Panel de Estado Offline / Antigüedad y Pendientes (Requerido para captura)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Sincronización: ${uiState.ultimaSincronizacion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Categorías sincronizadas: ${uiState.categorias.count { !it.pendiente }}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Cambios pendientes de subida: ${uiState.pendientesCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (uiState.pendientesCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                )
                if (uiState.enCursoSync) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                    onValueChange = {
                        nombreState = it
                        if (uiState.mensajeError != null || uiState.mensajeExito != null) {
                            viewModel.limpiarMensajes()
                        }
                    },
                    label = { Text("Nombre") },
                    isError = uiState.campoError == "nombre",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descripcionState,
                    onValueChange = {
                        descripcionState = it
                        if (uiState.mensajeError != null || uiState.mensajeExito != null) {
                            viewModel.limpiarMensajes()
                        }
                    },
                    label = { Text("Descripción") },
                    isError = uiState.campoError == "descripcion",
                    modifier = Modifier.fillMaxWidth()
                )

                // Mensaje de Error (Rojo)
                uiState.mensajeError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Mensaje de Éxito (Verde) - Solo si NO hay error activo
                if (uiState.mensajeError == null) {
                    uiState.mensajeExito?.let { exito ->
                        Text(
                            text = exito,
                            color = Color(0xFF1B5E20),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
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
                                descripcion = descripcionState,
                                onSuccessCallback = {
                                    nombreState = ""
                                    descripcionState = ""
                                }
                            )
                        },
                        enabled = !uiState.estaCargando && !uiState.enCursoSync
                    ) {
                        Text(if (uiState.categoriaEditandoId == null) "Guardar" else "Actualizar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (gestorPermisos != null) {
            Button(
                onClick = {
                    solicitandoPermiso = true
                    scope.launch {
                        try {
                            estadoNotificaciones = gestorPermisos.solicitar(Permiso.NOTIFICACIONES)
                        } finally {
                            solicitandoPermiso = false
                        }
                    }
                },
                enabled = !solicitandoPermiso,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gestionar Permiso de Notificaciones")
            }

            when (estadoNotificaciones) {
                EstadoPermiso.Concedido -> Text("Permiso de notificaciones concedido")
                EstadoPermiso.Denegado -> Text("Permiso de notificaciones denegado. Puedes volver a solicitarlo.")
                EstadoPermiso.DenegadoParaSiempre -> {
                    Text("Para permitir las notificaciones, habilítalas en Ajustes.")
                    OutlinedButton(onClick = gestorPermisos::abrirAjustes) {
                        Text("Abrir Ajustes")
                    }
                }
                null -> Unit
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Indicador de Carga
        if (uiState.estaCargando) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            Spacer(modifier = Modifier.height(8.dp))
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
                            Text(
                                text = if (categoria.pendiente) "ID local: ${categoria.id}" else "ID: ${categoria.id}",
                                style = MaterialTheme.typography.labelSmall
                            )
                            if (categoria.pendiente) {
                                Text(
                                    text = "Pendiente de sincronizar local",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
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