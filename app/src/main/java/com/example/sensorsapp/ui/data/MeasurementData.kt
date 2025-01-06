package com.example.sensorsapp.ui.data

data class MeasurementData(
    var sensorsData: MutableList<SensorData> = mutableListOf(),
    var timeTable: MutableList<Float> = mutableListOf(),
    var listOfSensors: MutableList<Int> = mutableListOf()
)

data class SensorData(
    var sensorType: Int = -2,
    var values: MutableList<DataFromSensor> = mutableListOf(),
)
