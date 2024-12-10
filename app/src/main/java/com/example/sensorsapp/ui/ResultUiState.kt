package com.example.sensorsapp.ui

data class ResultUiState(
    var gravAxis: Collection<Float> = mutableListOf(),
    var timeAxis: MutableList<Float> = mutableListOf(),
    var gyroAxis: Collection<Float> = mutableListOf()
)
