package com.example.sensorsapp.ui

data class MeasurementUiState(
    val isRunning: Boolean = false,
    var formattedTime: String = "00:00:000",
    val deleteConfirmation: Boolean = false,
    val gravityValues: List<Float> = listOf(0f,0f,0f),
    val gyroscopeValues: List<Float> = listOf(0f,0f,0f),
    val magneticValues: List<Float> = listOf(0f,0f,0f),
    val accelerometerValues: List<Float> = listOf(0f,0f,0f),
)
