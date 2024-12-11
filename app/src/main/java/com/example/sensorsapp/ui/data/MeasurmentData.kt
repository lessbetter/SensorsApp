package com.example.sensorsapp.ui.data

data class MeasurmentData(
    var sensorsData: MutableList<SensorData> = mutableListOf(),
    var timeTable: MutableList<Float> = mutableListOf(),
)

data class SensorData(
    var sensorType: Int = -2,
    var values: MutableList<List<Float>> = mutableListOf(),
)
