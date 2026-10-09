package pe.edu.upeu.pharmamobil.platform

import android.os.Build

actual class InfoDispositivo actual constructor() {
    actual val sistema: String = "Android ${Build.VERSION.RELEASE}"
    actual val version: String = "${Build.MANUFACTURER} ${Build.MODEL}"
}