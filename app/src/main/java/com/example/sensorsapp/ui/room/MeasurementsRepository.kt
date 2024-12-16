package com.example.sensorsapp.ui.room

import kotlinx.coroutines.flow.Flow

interface MeasurementsRepository {

    fun getAllMeasurementsStream(): Flow<List<Measurement>>

    fun getMeasurementStream(id: Int): Flow<Measurement?>

    suspend fun insertMeasurement(measurement: Measurement)

    suspend fun deleteMeasurement(measurement: Measurement)

    suspend fun updateMeasurement(measurement: Measurement)

}