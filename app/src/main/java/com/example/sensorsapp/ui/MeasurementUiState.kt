package com.example.sensorsapp.ui

data class MeasurementUiState(
    val isRunning: Boolean = false,
    var formattedTime: String = "00:00:000",
    val deleteConfirmation: Boolean = false
)
