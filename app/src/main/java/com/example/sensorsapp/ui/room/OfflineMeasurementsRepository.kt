package com.example.sensorsapp.ui.room

import kotlinx.coroutines.flow.Flow

class OfflineMeasurementsRepository(private val measurementDao: MeasurementDao): MeasurementsRepository {

    override fun getAllMeasurementsStream(): Flow<List<Measurement>> = measurementDao.getAllMeasurements()

    override fun getMeasurementStream(id: Int): Flow<Measurement?> = measurementDao.getMeasurement(id)

    override suspend fun insertMeasurement(measurement: Measurement) = measurementDao.insert(measurement)

    override suspend fun deleteMeasurement(measurement: Measurement) = measurementDao.delete(measurement)

    override suspend fun updateMeasurement(measurement: Measurement) = measurementDao.update(measurement)
}