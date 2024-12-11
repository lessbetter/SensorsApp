package com.example.sensorsapp.ui

sealed interface CreatingChartState {
    object Success: CreatingChartState
    object Loading: CreatingChartState
    object Error: CreatingChartState
}