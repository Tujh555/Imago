package io.tujh.imago.domain.ip

import android.os.Build

@JvmInline
value class Stand(val value: String) {
    val host get() = value.substringBefore(":")
    val port get() = value.substringAfter(":")

    companion object {
        val local = Stand("0.0.0.0:8080")
        val emulatorLocal = Stand("10.0.2.2:8080")

        fun computeLocal(): Stand {
            val isEmulator = (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                    || Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.HARDWARE.contains("goldfish")
                    || Build.HARDWARE.contains("ranchu")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || Build.PRODUCT.contains("sdk_google")
                    || Build.PRODUCT.contains("google_sdk")
                    || Build.PRODUCT.contains("sdk")
                    || Build.PRODUCT.contains("sdk_x86")
                    || Build.PRODUCT.contains("sdk_gphone64_arm64")
                    || Build.PRODUCT.contains("vbox86p")
                    || Build.PRODUCT.contains("emulator")
                    || Build.PRODUCT.contains("simulator")

            return if (isEmulator) emulatorLocal else local
        }
    }
}