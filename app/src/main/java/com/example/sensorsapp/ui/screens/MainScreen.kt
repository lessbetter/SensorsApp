package com.example.sensorsapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensorsapp.ui.data.MeasurementViewModel


//enum class SensorScreen(){
//    Start,
//    Sensors,
//    Measurement,
//    Result,
//    Confirmation,
//    Settings
//}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MeasurementViewModel = viewModel(),
    onNextButtonClicked: () -> Unit = {}
){
    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val(startButton, welcomeText, settingsButton) = createRefs()


        Text(
            modifier = Modifier.constrainAs(welcomeText){
                top.linkTo(parent.top, margin = 120.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            text="Welcome to the SensorApp",
            fontSize = 50.sp,
            lineHeight = 50.sp,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onNextButtonClicked,
            modifier = Modifier.constrainAs(startButton){
                top.linkTo(welcomeText.bottom,margin = 220.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text("Start new measurment")
        }
        FloatingActionButton(onClick = {/*TODO*/},modifier = Modifier.constrainAs(settingsButton){
            bottom.linkTo(parent.bottom,margin = 70.dp)
            start.linkTo(parent.start, margin = 30.dp)
        }) {
            Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(48.dp))
        }
    }
}

@Preview(showBackground = true,
    device = "spec:parent=pixel_9_pro,navigation=buttons", showSystemUi = true)
@Composable
fun MainScreenPreview(){
    MainScreen(
        modifier = Modifier.fillMaxSize()
    )
}

