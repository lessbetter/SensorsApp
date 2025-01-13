package com.example.sensorsapp.ui.screens

import android.hardware.Sensor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.sensorsapp.ui.data.MeasurementViewModel
import com.example.sensorsapp.ui.data.Sensors


@Composable
fun SelectSensorsScreen(
    modifier: Modifier = Modifier,
    viewModel: MeasurementViewModel,
    onNextButtonClicked: () -> Unit = {},
) {

    val sensorsUiState by viewModel.sensorsUiState.collectAsState()

    val gravityChecked = sensorsUiState.isGravityChecked
    val magneticChecked = sensorsUiState.isMagneticChecked
    val gyroscopeChecked = sensorsUiState.isGyroscopeChecked
    val accelerometerChecked = sensorsUiState.isAccelerometerChecked
    val showChecked = sensorsUiState.isShowSelected


    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        
        val (listText, sensorsColumn, nextButton,showTick) = createRefs()
        val topGuideLine = createGuidelineFromTop(0.2f)

        Text(text = "Select sensors you want to use:",
            modifier = Modifier.constrainAs(listText) {
                top.linkTo(topGuideLine)
                start.linkTo(parent.start, margin = 30.dp)
            })

        Column(modifier = Modifier.constrainAs(sensorsColumn) {
            top.linkTo(listText.bottom, margin = 30.dp)
            start.linkTo(listText.start)
        }) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ){
//                Text("Timer: ")
//
//                Checkbox(
//                    checked = checked,
//                    onCheckedChange = {checked = it}
//                )
//            }
            if (viewModel.listOfSensors.contains(Sensors(Sensor.TYPE_GRAVITY))) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gravity sensor: ")
                    //val index = sensorsUiState.listOfSensors.indexOf(Sensors(Sensor.STRING_TYPE_GRAVITY))
                    Checkbox(
                        checked = gravityChecked,
                        onCheckedChange = {
                            viewModel.onCheckedUpdate(Sensor.TYPE_GRAVITY, it)
                        }
                    )
                }
            }
            if (viewModel.listOfSensors.contains(Sensors(Sensor.TYPE_GYROSCOPE))) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gyroscope sensor: ")
                    //val index = sensorsUiState.listOfSensors.indexOf(Sensors(Sensor.STRING_TYPE_GYROSCOPE))
                    Checkbox(
                        checked = gyroscopeChecked,
                        onCheckedChange = {
                            viewModel.onCheckedUpdate(Sensor.TYPE_GYROSCOPE, it)
                        }
                    )
                }
            }
            if (viewModel.listOfSensors.contains(Sensors(Sensor.TYPE_MAGNETIC_FIELD))) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Magnetic field sensor: ")
                    //val index = sensorsUiState.listOfSensors.indexOf(Sensors(Sensor.STRING_TYPE_MAGNETIC_FIELD))
                    Checkbox(
                        checked = magneticChecked,
                        onCheckedChange = {
                            viewModel.onCheckedUpdate(Sensor.TYPE_MAGNETIC_FIELD, it)
                        }
                    )
                }
            }
            if (viewModel.listOfSensors.contains(Sensors(Sensor.TYPE_ACCELEROMETER))) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Accelerometer sensor: ")
                    //val index = sensorsUiState.listOfSensors.indexOf(Sensors(Sensor.STRING_TYPE_GYROSCOPE))
                    Checkbox(
                        checked = accelerometerChecked,
                        onCheckedChange = {
                            viewModel.onCheckedUpdate(Sensor.TYPE_ACCELEROMETER, it)
                        }
                    )
                }
            }

        }

        Row(
            modifier = Modifier.constrainAs(showTick){
            bottom.linkTo(nextButton.top, margin = 50.dp)
            start.linkTo(parent.start, margin = 10.dp)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show live sensor data: ")
            Checkbox(
                checked = showChecked,
                onCheckedChange = {
                    viewModel.showUpdate(it)
                }
            )
        }

        Button(
            onClick = onNextButtonClicked,
//            onClick = {
////                viewModel.startMeasuring()
////                scope.launch { viewModel.readData() }
//                      },
            modifier = Modifier.constrainAs(nextButton) {
                bottom.linkTo(parent.bottom, margin = 50.dp)
                end.linkTo(parent.end, margin = 30.dp)
            })
        {
            Text("Next")

        }

    }
}