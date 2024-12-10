package com.example.sensorsapp.ui.data

import kotlin.time.Duration

data class DataFromSensor(
    var timeMark: Duration? = null,
    var timestamp: Long = 0,
    var values: List<Float>? = null,
//    var v0: Float? = null,
//    var v1: Float = 0f,
//    var v2: Float = 0f
)
