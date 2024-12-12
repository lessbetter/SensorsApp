package com.example.sensorsapp.ui.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(measurement: Measurement)

    @Update
    suspend fun update(measurement: Measurement)

    @Delete
    suspend fun delete(measurement: Measurement)

    @Query("SELECT * from measurements where title = :title")
    fun getMeasurement(title: String): Flow<Measurement>

    @Query("SELECT * from measurements ORDER BY date ASC")
    fun getAllMeasurements(): Flow<List<Measurement>>
}