package com.example.sensorsapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sensorsapp.ui.screens.MainScreen
import com.example.sensorsapp.ui.data.MeasurementViewModel
import com.example.sensorsapp.ui.screens.MeasurementScreen
import com.example.sensorsapp.ui.screens.SelectSensorsScreen

enum class SensorScreen(){
    Start,
    Sensors,
    Measurement,
    Result,
    Confirmation,
    Settings
}

@Composable
fun SensorApp(
    modifier: Modifier = Modifier,
    viewModel: MeasurementViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
){
    val ctx = (LocalContext.current)
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination =  SensorScreen.Start.name,
            modifier = Modifier
        ){
            composable(route = SensorScreen.Start.name) {
                MainScreen(
                    modifier = modifier.fillMaxSize(),
                    onNextButtonClicked = {
                        viewModel.getSensors(ctx)
                        navController.navigate(SensorScreen.Sensors.name)
                    }
                )
            }
            composable(route = SensorScreen.Sensors.name) {
                SelectSensorsScreen(
                    modifier = modifier.fillMaxSize(),
                    viewModel,
                    onNextButtonClicked = {
                        navController.navigate(SensorScreen.Measurement.name)
                    }
                )
            }
            composable(route = SensorScreen.Measurement.name){
                MeasurementScreen(
                    modifier = modifier.fillMaxSize(),
                    viewModel,
                )
            }

        }
    }

}