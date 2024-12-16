package com.example.sensorsapp.ui.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sensorsapp.ui.data.MeasurementData

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val data: MeasurementData,
    val date: String
) {
}