package com.example.sensorsapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch


@Composable
fun SelectSensorsScreen(
    modifier: Modifier = Modifier,
    viewModel: MeasurementViewModel,
){
    val scope = rememberCoroutineScope()
    var checked by remember { mutableStateOf(true) }
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        val(listText,sensorsColumn,nextButton) = createRefs()
        val topGuideLine = createGuidelineFromTop(0.2f)

        Text(text = "Select sensors you want to use:",
            modifier = Modifier.constrainAs(listText){
                top.linkTo(topGuideLine)
                start.linkTo(parent.start, margin = 30.dp)
            })

        Column(modifier = Modifier.constrainAs(sensorsColumn){
            top.linkTo(listText.bottom, margin = 30.dp)
            start.linkTo(listText.start)
        }){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Text("Timer: ")

                Checkbox(
                    checked = checked,
                    onCheckedChange = {checked = it}
                )
            }
        }

        Button(
            onClick = {
                viewModel.startMeasuring()
                scope.launch { viewModel.readData() }
                      },
            modifier = Modifier.constrainAs(nextButton){
                bottom.linkTo(parent.bottom, margin = 40.dp)
                end.linkTo(parent.end, margin = 30.dp)
            })
        {
            Text("Next")

        }

    }
}