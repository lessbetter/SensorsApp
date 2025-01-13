package com.example.sensorsapp.ui.screens


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout






@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNextButtonClicked: () -> Unit = {},
    onShowButtonClicked: () -> Unit = {},
) {



    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (startButton, welcomeText, settingsButton, showButton) = createRefs()


        Text(
            modifier = Modifier.constrainAs(welcomeText) {
                top.linkTo(parent.top, margin = 120.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            text = "Welcome to the SensorApp",
            fontSize = 50.sp,
            lineHeight = 50.sp,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onNextButtonClicked,
            modifier = Modifier.constrainAs(startButton) {
                top.linkTo(welcomeText.bottom, margin = 220.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text("Start new measurment")
        }
        Button(
            onClick = onShowButtonClicked,
            modifier = Modifier.constrainAs(showButton) {
                top.linkTo(startButton.bottom, margin = 20.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text("Show collected data")
        }
    }
}

//@Preview(
//    showBackground = true,
//    device = "spec:parent=pixel_9_pro,navigation=buttons", showSystemUi = true
//)
//@Composable
//fun MainScreenPreview() {
//    MainScreen(
//        modifier = Modifier.fillMaxSize()
//    )
//}

