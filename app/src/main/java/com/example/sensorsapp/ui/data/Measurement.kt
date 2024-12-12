package com.example.sensorsapp.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val data: MeasurementData,
    val date: String
) {
}