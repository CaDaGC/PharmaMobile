package pe.edu.upeu.pharmamobil.data.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import pe.edu.upeu.pharmamobil.domain.port.Telemetria

class TelemetriaFirebase : Telemetria {
    override fun registrarError(e: Throwable, contexto: Map<String, String>) {
        try {
            val crashlyticsInstance = Firebase.crashlytics
            contexto.forEach { (clave, valor) ->
                crashlyticsInstance.setCustomKey(clave, valor)
            }
            crashlyticsInstance.recordException(e)
        } catch (_: Exception) {}
    }

    override fun registrarEvento(nombre: String, datos: Map<String, String>) {
        // Registro analítico complementario
    }
}