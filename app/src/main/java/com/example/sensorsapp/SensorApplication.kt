package com.example.sensorsapp

import android.app.Application
import com.example.sensorsapp.ui.data.AppContainer
import com.example.sensorsapp.ui.data.AppDataContainer

class SensorApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}