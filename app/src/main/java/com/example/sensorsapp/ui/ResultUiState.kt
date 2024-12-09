package com.example.sensorsapp.ui

data class ResultUiState(
    var yAxis: Collection<Float> = mutableListOf(),
    var timeAxis: Collection<Float> = mutableListOf(),
    var gyroAxis: Collection<Float> = mutableListOf()
)
