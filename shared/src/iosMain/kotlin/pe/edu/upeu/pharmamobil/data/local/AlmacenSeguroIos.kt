package pe.edu.upeu.pharmamobil.data.local

import platform.Foundation.NSUserDefaults

class AlmacenSeguroIos : AlmacenSeguro {
    private val prefs = NSUserDefaults.standardUserDefaults

    override fun guardarToken(token: String) {
        prefs.setObject(token, forKey = CLAVE_TOKEN)
        prefs.synchronize()
    }

    override fun leerToken(): String? {
        return prefs.stringForKey(CLAVE_TOKEN)
    }

    override fun borrarToken() {
        prefs.removeObjectForKey(CLAVE_TOKEN)
        prefs.synchronize()
    }

    private companion object {
        const val CLAVE_TOKEN = "jwt_token_seguro"
    }
}