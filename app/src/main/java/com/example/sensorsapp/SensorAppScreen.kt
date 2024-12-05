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
import com.example.sensorsapp.ui.MainScreen
import com.example.sensorsapp.ui.MeasurementViewModel
import com.example.sensorsapp.ui.SelectSensorsScreen

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
                    modifier = Modifier.fillMaxSize(),
                    onNextButtonClicked = {
                        viewModel.getSensors(ctx)
                        navController.navigate(SensorScreen.Sensors.name)
                    }
                )
            }
            composable(route = SensorScreen.Sensors.name) {
                SelectSensorsScreen(
                    modifier = Modifier.fillMaxSize(),
                    viewModel
                )
            }

        }
    }

}