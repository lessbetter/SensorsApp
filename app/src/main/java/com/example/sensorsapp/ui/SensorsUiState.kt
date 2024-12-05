package com.example.sensorsapp.ui

data class SensorsUiState(
    val listOfSensors: MutableList<Boolean> = MutableList(1){true}
)
