package pe.edu.upeu.pharmamobile.domain.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pe.edu.upeu.pharmamobile.domain.model.Producto
import pe.edu.upeu.pharmamobile.domain.result.ResultadoProductos

class ProductoService {
    private val productosSimulados = listOf(
        Producto(1, "Paracetamol", 8.50, 100),
        Producto(2, "Ibuprofeno", 12.00, 50),
        Producto(3, "Amoxicilina", 18.50, 20)
    )

    suspend fun obtenerProductos(): List<Producto> {
        delay(1000)
        return productosSimulados
    }

    fun observarEstados(): Flow<String> = flow {
        emit("Iniciando consulta de inventario...")
        delay(1000)
        emit("Procesando datos...")
        delay(1000)
        emit("Finalizado")
    }

    fun observarProductos(): Flow<List<Producto>> = flow {
        emit(emptyList())
        delay(1000)
        emit(productosSimulados)
        delay(1500)

        val productosActualizados = productosSimulados.map { producto ->
            if (producto.id == 1L) {
                producto.copy(stock = producto.stock - 5)
            } else {
                producto
            }
        }
        emit(productosActualizados)
    }

    fun cargarProductos(): Flow<ResultadoProductos> = flow {
        emit(ResultadoProductos.Cargando)
        delay(1000)
        try {
            emit(ResultadoProductos.Exito(productosSimulados))
        } catch (e: Exception) {
            emit(ResultadoProductos.Error("Error al cargar productos: ${e.message}"))
        }
    }
}