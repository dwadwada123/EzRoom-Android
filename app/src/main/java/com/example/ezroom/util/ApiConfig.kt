package com.example.ezroom.util

import android.os.Build

object ApiConfig {
    // Base URL configuration for emulator and physical device
    private const val BASE_URL_DEVICE = "http://192.168.2.37:3000/"
    private const val BASE_URL_EMULATOR = "http://10.0.2.2:3000/"

    // Check if running on emulator
    private fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.FINGERPRINT.contains("google/sdk")
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

    // Get API base URL
    fun getBaseUrl(): String {
        return if (isEmulator()) BASE_URL_EMULATOR else BASE_URL_DEVICE
    }
}
