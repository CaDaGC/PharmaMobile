package pe.edu.upeu.pharmamobil.data.local

import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SincronizacionPreferences {
    private var ultimoTimestampEpoch: Long? = null

    fun registrarSincronizacionExitosa() {
        ultimoTimestampEpoch = Clock.System.now().toEpochMilliseconds()
    }

    fun obtenerTextoUltimaSincronizacion(): String {
        val epoch = ultimoTimestampEpoch ?: return "Nunca sincronizado"

        val instant = Instant.fromEpochMilliseconds(epoch)
        val tz = TimeZone.currentSystemDefault()
        val localDateTime = instant.toLocalDateTime(tz)

        val hora = localDateTime.hour.toString().padStart(2, '0')
        val minuto = localDateTime.minute.toString().padStart(2, '0')
        val dia = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val mes = localDateTime.monthNumber.toString().padStart(2, '0')

        return "Actualizado $dia/$mes a las $hora:$minuto"
    }
}