package com.example.sensorsapp

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.sensorsapp.ui.data.AppContainer
import com.example.sensorsapp.ui.data.AppDataContainer
import com.example.sensorsapp.ui.data.UserPreferencesRepository

private const val SENSOR_PREFERENCE_NAME = "sensor_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SENSOR_PREFERENCE_NAME
)

class SensorApplication: Application() {
    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}