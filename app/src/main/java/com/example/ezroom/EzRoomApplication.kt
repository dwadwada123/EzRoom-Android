package com.example.ezroom

import android.app.Application
import android.content.Context

class EzRoomApplication : Application() {
    companion object {
        private var instance: EzRoomApplication? = null
        
        fun getContext(): Context {
            return instance?.applicationContext ?: throw IllegalStateException("EzRoomApplication not initialized. Avoid calling getContext() in Compose Previews.")
        }
        
        fun isInitialized(): Boolean = instance != null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
