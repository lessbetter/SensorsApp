package com.example.sensorsapp.ui.room

import androidx.room.TypeConverter
import com.example.sensorsapp.ui.data.MeasurementData
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromString(value: String?): MeasurementData?{
        return value?.let{ Gson().fromJson(it, MeasurementData::class.java) }
    }

    @TypeConverter
    fun measurementDataToString(value: MeasurementData?): String?{
        return value?.let{ Gson().toJson(it) }
    }
}