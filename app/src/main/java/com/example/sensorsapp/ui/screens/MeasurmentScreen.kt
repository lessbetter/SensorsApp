package com.example.sensorsapp.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.sensorsapp.R
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
        val (stoper, startPauseButton, stopButton, nextButton) = createRefs()
        val topGuideLine = createGuidelineFromTop(0.2f)

        Text(
            text = measurementUiState.formattedTime,
            modifier = Modifier.constrainAs(stoper) {
                top.linkTo(topGuideLine)
                start.linkTo(parent.start, margin = 30.dp)
            },
            fontSize = 70.sp
        )
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