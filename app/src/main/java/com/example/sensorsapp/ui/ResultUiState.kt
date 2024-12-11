package com.example.sensorsapp.ui

data class ResultUiState(
    var gravAxis: MutableList<List<Float>> = mutableListOf(),
    var gyroAxis: MutableList<List<Float>> = mutableListOf(),
    var magneAxis: MutableList<List<Float>> = mutableListOf(),
    var acceAxis: MutableList<List<Float>> = mutableListOf(),
    var timeAxis: MutableList<Float> = mutableListOf(),
)
