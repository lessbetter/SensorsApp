package com.example.sensorsapp

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sensorsapp.ui.AppViewModelProvider
import com.example.sensorsapp.ui.data.MeasurementViewModel
import com.example.sensorsapp.ui.screens.ChartsScreen
import com.example.sensorsapp.ui.screens.MainScreen
import com.example.sensorsapp.ui.screens.MeasurementScreen
import com.example.sensorsapp.ui.screens.SavedDataScreen
import com.example.sensorsapp.ui.screens.SelectSensorsScreen
import com.example.sensorsapp.ui.screens.measurement.MeasurementDetailsDestination
import com.example.sensorsapp.ui.screens.measurement.MeasurementDetailsScreen
import kotlinx.coroutines.launch

enum class SensorScreen {
    Start,
    Sensors,
    Measurement,
    Result,
//    Confirmation,
//    Settings,
    Show
}

@Composable
fun SensorApp(
    modifier: Modifier = Modifier,
    viewModel: MeasurementViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navController: NavHostController = rememberNavController(),
){
    val coroutineScope = rememberCoroutineScope()
    val ctx = (LocalContext.current)
    val chartState = viewModel.creatingChartState
    //val stopWatch = remember{ StopWatch(viewModel)}
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
                    },
                    onShowButtonClicked = { navController.navigate(SensorScreen.Show.name) }
                )
            }
            composable(route = SensorScreen.Sensors.name) {
                SelectSensorsScreen(
                    modifier = modifier.fillMaxSize(),
                    viewModel,
                    onNextButtonClicked = {
                        viewModel.setLocalList()
                        if(viewModel.selectedSensors.isEmpty()){
                            Toast.makeText(ctx,"You have to select at least one sensor!",Toast.LENGTH_SHORT).show()
                        }else{
                            viewModel.resetSelectedSensors()
                            navController.navigate(SensorScreen.Measurement.name)
                        }

                    }
                )
            }
            composable(route = SensorScreen.Measurement.name){
                MeasurementScreen(
                    modifier = modifier.fillMaxSize(),
                    viewModel,
                    onNextButtonClicked = {
                        if(!viewModel.hasStarted){
                            Toast.makeText(ctx,"You haven't started yet",Toast.LENGTH_SHORT).show()

                        }else{
                            viewModel.toAxis()
                            navController.navigate(SensorScreen.Result.name)
                            viewModel.saveTime()
                        }

                    },

                )

            }
            composable(route = SensorScreen.Result.name) {
                ChartsScreen(
                    chartState,
                    viewModel,
                    modifier = modifier.fillMaxSize(),
                    onSaveButtonClicked = {
                        coroutineScope.launch{
                            viewModel.saveData(it)
                            //navController.navigate(SensorScreen.Start.name)
                            navController.popBackStack(SensorScreen.Start.name,false)

                        }
                    }
                )
            }
            composable(route = SensorScreen.Show.name){
                SavedDataScreen(
                    navigateToEntry = {},
                    navigateToUpdate = {navController.navigate("${MeasurementDetailsDestination.route}/${it}")},
                )
            }
            composable(
                route = MeasurementDetailsDestination.routeWithArgs,
                arguments = listOf(navArgument(MeasurementDetailsDestination.itemIdArg) {
                    type = NavType.IntType
                })
            ) {
                MeasurementDetailsScreen(
//                    navigateToEditItem =
//                    {
//                        navController.navigate("${ItemEditDestination.route}/$it")
//                    },
//                    navigateBack = { navController.navigateUp() }
                )
            }

        }
    }

}