package pe.edu.upeu.pharmamobil.data.local

import platform.Foundation.NSUserDefaults

class AlmacenSeguroIos : AlmacenSeguro {
    private val prefs = NSUserDefaults.standardUserDefaults

    override fun guardarToken(token: String) {
        prefs.setObject(token, forKey = CLAVE)
        prefs.synchronize()
    }

    override fun leerToken(): String? {
        return prefs.stringForKey(CLAVE)
    }

    override fun borrarToken() {
        prefs.removeObjectForKey(CLAVE)
        prefs.synchronize()
    }

    private companion object {
        const val CLAVE = "jwt_token_seguro"
    }
}