package com.example.sensorsapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.sensorsapp.ui.data.MeasurementViewModel

@Composable
fun MeasurementScreen(
    modifier: Modifier,
    viewModel: MeasurementViewModel
){
    val measurementUiState by viewModel.measurementUiState.collectAsState()
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val(stoper,startPauseButton,stopButton) = createRefs()
        val topGuideLine = createGuidelineFromTop(0.2f)

        Text(text = "00:00:00",
        modifier = Modifier.constrainAs(stoper){
            top.linkTo(topGuideLine)
            start.linkTo(parent.start, margin = 30.dp)
        })
        Button() { }
    }

}