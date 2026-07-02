package com.example.ezroom.util

import android.os.Build

object ApiConfig {
    private fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
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
                || Build.PRODUCT.contains("emulator")
    }

    fun getBaseUrl(): String {
        return if (isEmulator()) {
            "http://10.0.2.2:3000/"  // IP trỏ ngược về localhost từ máy ảo
        } else {
            "http://192.168.2.12:3000/" // IP máy tính của tôi trong mạng Wi-Fi local
        }
    }
}
