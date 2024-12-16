package com.example.sensorsapp.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sensorsapp.SensorApplication
import com.example.sensorsapp.ui.data.MeasurementViewModel
import com.example.sensorsapp.ui.data.SavedDataViewModel
import com.example.sensorsapp.ui.data.SetNameViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory{
        initializer{
            MeasurementViewModel(sensorApplication().container.measurementsRepository)
        }
        initializer{
            SetNameViewModel()
        }
        initializer {
            SavedDataViewModel(sensorApplication().container.measurementsRepository)
        }
    }
}

fun CreationExtras.sensorApplication(): SensorApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SensorApplication)