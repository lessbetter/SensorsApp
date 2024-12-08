package com.example.sensorsapp.ui

data class ResultUiState(
    var yAxis: Collection<Float> = mutableListOf(),
    var xAxis: Collection<Long> = mutableListOf(),
    var gyroAxis: Collection<Float> = mutableListOf()
)
