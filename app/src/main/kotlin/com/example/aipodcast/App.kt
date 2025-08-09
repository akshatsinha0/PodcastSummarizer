package com.example.aipodcast

import android.app.Application
import android.util.Log

class App : Application() {
    lateinit var appContainer: AppContainer
    
    override fun onCreate() {
        super.onCreate()
        try {
            appContainer = AppContainer(this)
            Log.d("App", "AppContainer initialized successfully")
        } catch (e: Exception) {
            Log.e("App", "Failed to initialize AppContainer", e)
            throw e
        }
    }
}