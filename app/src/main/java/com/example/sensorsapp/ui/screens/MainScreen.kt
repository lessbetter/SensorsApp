package com.example.sensorsapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.sensorsapp.SensorScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


//enum class SensorScreen(){
//    Start,
//    Sensors,
//    Measurement,
//    Result,
//    Confirmation,
//    Settings
//}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    //viewModel: MeasurementViewModel = viewModel(),
    onNextButtonClicked: () -> Unit = {},
    onShowButtonClicked: () -> Unit = {},
    //navController: NavController
) {


    val filePermissionState = rememberPermissionState(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)

    //val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            // Handle permission denial
        }
    }

    LaunchedEffect(filePermissionState) {
        if (!filePermissionState.status.isGranted && filePermissionState.status.shouldShowRationale) {
            // Show rationale if needed
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }
    }

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
        if (filePermissionState.status.isGranted) {
            Text("File permission Granted")
        } else {
            Column {
                val textToShow = if (filePermissionState.status.shouldShowRationale) {
                    // If the user has denied the permission but the rationale can be shown,
                    // then gently explain why the app requires this permission
                    "The camera is important for this app. Please grant the permission."
                } else {
                    // If it's the first time the user lands on this feature, or the user
                    // doesn't want to be asked again for this permission, explain that the
                    // permission is required
                    "Camera permission required for this feature to be available. " +
                            "Please grant the permission"
                }
                Text(textToShow)
                Button(onClick = { filePermissionState.launchPermissionRequest() }) {
                    Text("Request permission")
                }
            }
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

