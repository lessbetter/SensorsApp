package com.example.sensorsapp.ui.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

data class UserPreferences(
    val isGravitySelected: Boolean,
    val isGyroscopeSelected: Boolean,
    val isMagneticSelected: Boolean,
    val isAccelerometerSelected: Boolean,
    val isValuesShow: Boolean,
)

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object{
        val IS_GRAVITY_SELECTED = booleanPreferencesKey("is_gravity_selected")
        val IS_GYROSCOPE_SELECTED = booleanPreferencesKey("is_gyroscope_selected")
        val IS_MAGNETIC_SELECTED = booleanPreferencesKey("is_magnetic_selected")
        val IS_ACCELEROMETER_SELECTED = booleanPreferencesKey("is_accelerometer_selected")
        val IS_VALUES_SHOW = booleanPreferencesKey("is_values_show")
        const val TAG = "UserPreferencesRepo"
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val isGravitySelected = preferences[IS_GRAVITY_SELECTED] ?: false
            val isGyroscopeSelected = preferences[IS_GYROSCOPE_SELECTED] ?: false
            val isMagneticSelected = preferences[IS_MAGNETIC_SELECTED] ?: false
            val isAccelerometerSelected = preferences[IS_ACCELEROMETER_SELECTED] ?: false
            val isValuesShow = preferences[IS_VALUES_SHOW] ?: false

            UserPreferences(isGravitySelected,isGyroscopeSelected,isMagneticSelected,isAccelerometerSelected,isValuesShow)
        }

    val isGravitySelected: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_GRAVITY_SELECTED] ?: false
        }
    val isGyroscopeSelected: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_GYROSCOPE_SELECTED] ?: false
        }

    val isMagneticSelected: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_MAGNETIC_SELECTED] ?: false
        }

    val isAccelerometerSelected: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_ACCELEROMETER_SELECTED] ?: false
        }

    val isShowValuesSelected: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_VALUES_SHOW] ?: false
        }

    suspend fun saveGravityPreference(isGravitySelected: Boolean){
        dataStore.edit { preferences ->
            preferences[IS_GRAVITY_SELECTED] = isGravitySelected
        }
    }

    suspend fun saveGyroscopePreference(isGyroscopeSelected: Boolean){
        dataStore.edit{ preferences ->
            preferences[IS_GYROSCOPE_SELECTED] = isGyroscopeSelected
        }
    }

    suspend fun saveMagneticPreference(isMagneticSelected: Boolean){
        dataStore.edit { preferences ->
            preferences[IS_MAGNETIC_SELECTED] = isMagneticSelected
        }
    }

    suspend fun saveAccelerometerPreference(isAccelerometerSelected: Boolean){
        dataStore.edit { preferences ->
            preferences[IS_ACCELEROMETER_SELECTED] = isAccelerometerSelected
        }
    }

    suspend fun saveShowValuesPreference(isShowSelected: Boolean){
        dataStore.edit { preferences ->
            preferences[IS_VALUES_SHOW] = isShowSelected
        }
    }
}