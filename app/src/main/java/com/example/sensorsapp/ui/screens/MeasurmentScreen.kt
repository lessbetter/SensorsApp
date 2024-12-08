package com.example.sensorsapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.sensorsapp.R
import com.example.sensorsapp.ui.data.MeasurementViewModel

@Composable
fun MeasurementScreen(
    modifier: Modifier,
    viewModel: MeasurementViewModel,
    onNextButtonClicked: () -> Unit = {}
){
    val measurementUiState by viewModel.measurementUiState.collectAsState()
    val isRunning = measurementUiState.isRunning
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val(stoper,startPauseButton,stopButton,nextButton) = createRefs()
        val topGuideLine = createGuidelineFromTop(0.2f)

        Text(text = "00:00:00",
        modifier = Modifier.constrainAs(stoper){
            top.linkTo(topGuideLine)
            start.linkTo(parent.start, margin = 30.dp)
        })
        Button(
            onClick = { viewModel.startMeasuring() },
            modifier = Modifier.fillMaxWidth()
                .constrainAs(startPauseButton){
                    bottom.linkTo(stopButton.top, margin = 30.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(if(isRunning) stringResource(R.string.pause) else stringResource(R.string.start))
        }
        Button(onClick = {viewModel.stopRunning()},
            modifier = Modifier.fillMaxWidth()
                .constrainAs(stopButton){
                    bottom.linkTo(nextButton.top, margin = 30.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            Text(stringResource(R.string.stop))
        }
        Button(onClick = onNextButtonClicked,
            modifier = Modifier.constrainAs(nextButton){
                bottom.linkTo(parent.bottom, margin = 60.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }){
            Text(stringResource(R.string.next))
        }
    }

}