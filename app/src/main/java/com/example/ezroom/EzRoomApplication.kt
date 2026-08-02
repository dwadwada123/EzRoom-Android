package com.example.ezroom

import android.app.Application
import android.content.Context

class EzRoomApplication : Application() {
    companion object {
        private lateinit var instance: EzRoomApplication
        fun getContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
