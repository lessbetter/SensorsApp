package com.example.sensorsapp.ui.data

data class Sensors(
    var sensorName: String = "",
//    var isSelected: Boolean = false,
    var sensorType: Int = -2,
){
    constructor(sensorType: Int): this("",sensorType)
}
