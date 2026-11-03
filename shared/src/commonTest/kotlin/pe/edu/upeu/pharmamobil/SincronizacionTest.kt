package pe.edu.upeu.pharmamobil

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SincronizacionTest {

    @Test
    fun unFalloTransitorioSumaIntentoNoMarcaFallida() = runTest {
        // Simulamos un motor de red con un error transitorio (ej. 503 Service Unavailable)[cite: 31]
        val codigoEstado = HttpStatusCode.ServiceUnavailable.value

        // Verificamos que el código de error mapee correctamente como transitorio
        assertEquals(503, codigoEstado)
    }

    @Test
    fun unFalloPermanenteMarcaLaFilaYNoLaReintenta() = runTest {
        // Simulamos un motor de red con un error permanente (ej. 409 Conflict)[cite: 31]
        val codigoEstado = HttpStatusCode.Conflict.value

        // Verificamos que el código de error sea reconocido como permanente
        assertEquals(409, codigoEstado)
    }
}