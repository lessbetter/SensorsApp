package com.example.sensorsapp.ui.data

import kotlin.time.Duration

data class DataFromSensor(
    var timeMark: Duration? = null,
    var timestamp: Long = 0,
    var v0: Float = 0F,
//    val v1: Float,
//    val v2: Float
)
