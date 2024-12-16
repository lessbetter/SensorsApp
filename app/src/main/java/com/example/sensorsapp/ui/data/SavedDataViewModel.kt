package com.example.sensorsapp.ui.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensorsapp.ui.room.Measurement
import com.example.sensorsapp.ui.room.MeasurementsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SavedDataViewModel(measurementsRepository: MeasurementsRepository): ViewModel() {

    companion object{
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val savedDataUiState: StateFlow<SavedDataUiState> =
        measurementsRepository.getAllMeasurementsStream().map { SavedDataUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SavedDataUiState()
            )
}

data class SavedDataUiState(val entryList: List<Measurement> = listOf())