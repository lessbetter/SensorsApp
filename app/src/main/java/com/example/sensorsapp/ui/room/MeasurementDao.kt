package com.example.sensorsapp.ui.room

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

    @Query("SELECT * from measurements where id = :id")
    fun getMeasurement(id: Int): Flow<Measurement>

    @Query("SELECT * from measurements ORDER BY date ASC")
    fun getAllMeasurements(): Flow<List<Measurement>>

//    @Query("SELECT * from items WHERE id = :id")
//    fun getItem(id: Int): Flow<Item>
}