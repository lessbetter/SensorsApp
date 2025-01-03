package com.example.sensorsapp.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensorsapp.R
import com.example.sensorsapp.ui.AppViewModelProvider
import com.example.sensorsapp.ui.data.MeasurementViewModel

@Composable
fun MeasurementScreen(
    modifier: Modifier,
    viewModel: MeasurementViewModel,
    onNextButtonClicked: () -> Unit = {},
    //stopwatch: StopWatch
) {
    val measurementUiState by viewModel.measurementUiState.collectAsState()
    val isRunning = measurementUiState.isRunning
    val deleteConfirmation = measurementUiState.deleteConfirmation
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (stoper, startPauseButton, stopButton, nextButton,senValues) = createRefs()
        val topGuideLine = createGuidelineFromTop(0.2f)

        Text(
            text = measurementUiState.formattedTime,
            modifier = Modifier.constrainAs(stoper) {
                top.linkTo(topGuideLine)
                start.linkTo(parent.start, margin = 30.dp)
            },
            fontSize = 70.sp
        )
        Column(
            modifier = Modifier.constrainAs(senValues) {
                top.linkTo(stoper.bottom, margin = 20.dp)
                start.linkTo(parent.start, margin = 10.dp)
            },
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(vertical = 5.dp),
            ) {
                Column {
                    Row{
                        Text("Gravity force along:")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text("X Axis: ${measurementUiState.gravityValues[0]}",Modifier.weight(1f))
                        Text("Y Axis: ${measurementUiState.gravityValues[1]}",Modifier.weight(1f))
                        Text("Z Axis: ${measurementUiState.gravityValues[2]}",Modifier.weight(1f))
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Column {
                    Row{
                        Text("Rate of rotation around:")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text("X Axis: ${measurementUiState.accelerometerValues[0]}",Modifier.weight(1f))
                        Text("Y Axis: ${measurementUiState.accelerometerValues[1]}",Modifier.weight(1f))
                        Text("Z Axis: ${measurementUiState.accelerometerValues[2]}",Modifier.weight(1f))
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Column {
                    Row{
                        Text("Geomagnetic field strength along:")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text("X Axis: ${measurementUiState.magneticValues[0]}",Modifier.weight(1f))
                        Text("Y Axis: ${measurementUiState.magneticValues[1]}",Modifier.weight(1f))
                        Text("Z Axis: ${measurementUiState.magneticValues[2]}",Modifier.weight(1f))
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Column {
                    Row{
                        Text("Acceleration force along along:")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text("X Axis: ${measurementUiState.accelerometerValues[0]}",Modifier.weight(1f))
                        Text("Y Axis: ${measurementUiState.accelerometerValues[1]}",Modifier.weight(1f))
                        Text("Z Axis: ${measurementUiState.accelerometerValues[2]}",Modifier.weight(1f))
                    }
                }
            }
        }
        Button(
            onClick = {
                viewModel.startMeasuring()
                //if(isRunning)stopwatch.pause() else stopwatch.start()
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(startPauseButton) {
                    bottom.linkTo(stopButton.top, margin = 30.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(if (isRunning) stringResource(R.string.pause) else stringResource(R.string.start))
        }
        Button(onClick = {
            viewModel.updateDeleteConfirmation(true)
//            viewModel.stopRunning()
            //stopwatch.reset()
        },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(stopButton) {
                    bottom.linkTo(nextButton.top, margin = 30.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            Text(stringResource(R.string.reset))
        }
        Button(onClick = onNextButtonClicked,
            modifier = Modifier.constrainAs(nextButton) {
                bottom.linkTo(parent.bottom, margin = 60.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text(stringResource(R.string.next))
        }
        if(deleteConfirmation){
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    viewModel.updateDeleteConfirmation(false)
                    viewModel.stopRunning()
                },
                onDeleteCancel = { viewModel.updateDeleteConfirmation(false) },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }

}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text("Attention") },
        text = { Text("Are you sure you want to delete?") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = "No")
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = "Yes")
            }
        })
}
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true, showSystemUi = true
)
@Composable
fun PreviewMeasurement(){
    //val measurementUiState by viewModel.measurementUiState.collectAsState()
    //val isRunning = measurementUiState.isRunning
    //val deleteConfirmation = measurementUiState.deleteConfirmation
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (stoper, startPauseButton, stopButton, nextButton,senValues) = createRefs()
        val topGuideLine = createGuidelineFromTop(0.2f)

        Text(
            text = "00:00:000",
            modifier = Modifier.constrainAs(stoper) {
                top.linkTo(topGuideLine)
                start.linkTo(parent.start, margin = 30.dp)
            },
            fontSize = 70.sp
        )
        Column(
            modifier = Modifier.constrainAs(senValues) {
                top.linkTo(stoper.bottom, margin = 20.dp)
                start.linkTo(parent.start, margin = 10.dp)
            },
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(vertical = 5.dp),
            ) {
                Column {
                    Row{
                        Text("Gravity force along:")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text("X Axis: 0.000",Modifier.weight(1f))
                        Text("Y Axis: 0.000",Modifier.weight(1f))
                        Text("Z Axis: 0.000",Modifier.weight(1f))
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Column {
                    Row{
                        Text("Rate of rotation around:")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text("X Axis: 0.000",Modifier.weight(1f))
                        Text("Y Axis: 0.000",Modifier.weight(1f))
                        Text("Z Axis: 0.000",Modifier.weight(1f))
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Column {
                    Row{
                        Text("Geomagnetic field strength along:")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text("X Axis: 0.000",Modifier.weight(1f))
                        Text("Y Axis: 0.000",Modifier.weight(1f))
                        Text("Z Axis: 0.000",Modifier.weight(1f))
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(vertical = 5.dp)
            ) {
                Column {
                    Row{
                        Text("Acceleration force along along:")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text("X Axis: 0.000",Modifier.weight(1f))
                        Text("Y Axis: 0.000",Modifier.weight(1f))
                        Text("Z Axis: 0.000",Modifier.weight(1f))
                    }
                }
            }
        }
        Button(
            onClick = {
                //viewModel.startMeasuring()
                //if(isRunning)stopwatch.pause() else stopwatch.start()
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(startPauseButton) {
                    bottom.linkTo(stopButton.top, margin = 30.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(stringResource(R.string.pause))
        }
        Button(onClick = {
            //viewModel.updateDeleteConfirmation(true)
//            viewModel.stopRunning()
            //stopwatch.reset()
        },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(stopButton) {
                    bottom.linkTo(nextButton.top, margin = 30.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            Text(stringResource(R.string.reset))
        }
        Button(onClick = {  },
            modifier = Modifier.constrainAs(nextButton) {
                bottom.linkTo(parent.bottom, margin = 60.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text(stringResource(R.string.next))
        }
        if(false){
            DeleteConfirmationDialog(
                onDeleteConfirm = {
//                    viewModel.updateDeleteConfirmation(false)
//                    viewModel.stopRunning()
                },
                onDeleteCancel = {  },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }

}