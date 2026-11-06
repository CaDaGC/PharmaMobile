package pe.edu.upeu.pharmamobil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pe.edu.upeu.pharmamobil.data.permiso.GestorPermisosAndroid
import pe.edu.upeu.pharmamobil.data.permiso.PuentePermisos

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val gestor = GestorPermisosAndroid(applicationContext).apply {
            puente = PuentePermisos(this@MainActivity)
        }
        setContent {
            App(gestorPermisos = gestor)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}