package com.example.sensorsapp.ui

import com.example.sensorsapp.ui.data.Sensors

data class SensorsUiState(
    val listOfSensors: MutableList<Sensors> = mutableListOf(),
    val isGravityChecked: Boolean = false,
    val isMagneticChecked: Boolean = false,
    val isGyroscopeChecked: Boolean = false,
    val isAccelerometerChecked: Boolean = false,
    //val isChecked: ((String) -> Boolean) = { false }
)
