package pe.edu.upeu.pharmamobil.data.permiso

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CompletableDeferred
import pe.edu.upeu.pharmamobil.domain.permiso.EstadoPermiso
import pe.edu.upeu.pharmamobil.domain.permiso.GestorPermisos
import pe.edu.upeu.pharmamobil.domain.permiso.Permiso

class PuentePermisos(val actividad: ComponentActivity) {
    private var pendiente: CompletableDeferred<Boolean>? = null
    private val lanzador = actividad.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> pendiente?.complete(concedido) }

    suspend fun pedir(nombre: String): Boolean {
        val espera = CompletableDeferred<Boolean>()
        pendiente = espera
        lanzador.launch(nombre)
        return espera.await()
    }
}

class GestorPermisosAndroid(
    private val contexto: Context
) : GestorPermisos {
    var puente: PuentePermisos? = null

    private fun nombreDeSistema(permiso: Permiso): String? = when (permiso) {
        Permiso.NOTIFICACIONES -> if (android.os.Build.VERSION.SDK_INT >= 33) {
            android.Manifest.permission.POST_NOTIFICATIONS
        } else null
    }

    override suspend fun estado(permiso: Permiso): EstadoPermiso {
        val nombre = nombreDeSistema(permiso) ?: return EstadoPermiso.Concedido
        val concedido = ContextCompat.checkSelfPermission(contexto, nombre) == PackageManager.PERMISSION_GRANTED
        if (concedido) return EstadoPermiso.Concedido

        val actividad = puente?.actividad ?: return EstadoPermiso.Denegado
        val procedeExplicar = ActivityCompat.shouldShowRequestPermissionRationale(actividad, nombre)

        return if (procedeExplicar) {
            EstadoPermiso.Denegado
        } else {
            EstadoPermiso.DenegadoParaSiempre
        }
    }

    override suspend fun solicitar(permiso: Permiso): EstadoPermiso {
        val nombre = nombreDeSistema(permiso) ?: return EstadoPermiso.Concedido
        val concedido = puente?.pedir(nombre) ?: false
        return if (concedido) EstadoPermiso.Concedido else estado(permiso)
    }

    override fun abrirAjustes() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", contexto.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        contexto.startActivity(intent)
    }
}