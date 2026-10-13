package pe.edu.upeu.pharmamobil.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AlmacenSeguroAndroid(context: Context) : AlmacenSeguro {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_token_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun guardarToken(token: String) {
        sharedPreferences.edit().putString(CLAVE_TOKEN, token).apply()
    }

    override fun leerToken(): String? {
        return sharedPreferences.getString(CLAVE_TOKEN, null)
    }

    override fun borrarToken() {
        sharedPreferences.edit().remove(CLAVE_TOKEN).apply()
    }

    private companion object {
        const val CLAVE_TOKEN = "jwt_token_seguro"
    }
}