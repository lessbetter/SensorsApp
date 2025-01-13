package com.example.sensorsapp

import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sensorsapp.ui.theme.SensorsAppTheme


class MainActivity : ComponentActivity() {
    //lateinit var sensorManager: SensorManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        val requestPermissionLauncher =
//            registerForActivityResult(
//                ActivityResultContracts.RequestPermission()
//            ) { isGranted: Boolean ->
//                if (isGranted) {
//                    // Permission is granted. Continue the action or workflow in your
//                    // app.
//                } else {
//                    // Explain to the user that the feature is unavailable because the
//                    // feature requires a permission that the user has denied. At the
//                    // same time, respect the user's decision. Don't link to system
//                    // settings in an effort to convince the user to change their
//                    // decision.
//                }
//            }
//
//        when {
//            ContextCompat.checkSelfPermission(
//                this,
//                MANAGE_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                // You can use the API that requires the permission.
//            }
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                this, MANAGE_EXTERNAL_STORAGE) -> {
//                // In an educational UI, explain to the user why your app requires this
//                // permission for a specific feature to behave as expected, and what
//                // features are disabled if it's declined. In this UI, include a
//                // "cancel" or "no thanks" button that lets the user continue
//                // using your app without granting the permission.
//                //showInContextUI(...)
//            }
//            else -> {
//                // You can directly ask for the permission.
//                // The registered ActivityResultCallback gets the result of this request.
//                requestPermissionLauncher.launch(
//                    MANAGE_EXTERNAL_STORAGE)
//            }
//        }

        //val viewModel: MeasurementViewModel = viewModel()

        //sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
//        deviceSensors.forEach{
//            Log.d("test",it.stringType)
//        }
        setContent {
            SensorsAppTheme {
                SensorApp()
                //MainActivityPreview()
            }
        }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Composable
//fun GreetingText(message: String, modifier: Modifier = Modifier){
//    Text(
//        text = message,
//        fontSize = 50.sp,
//        lineHeight = 50.sp,
//        textAlign = TextAlign.Center
//    )
//}
//
//@Composable
//fun MenuButtons(modifier: Modifier = Modifier){
//    Column(
//        modifier = modifier,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ){
//        Text(
//            text = "Welcome to the sensors app",
//            fontSize = 50.sp,
//            lineHeight = 50.sp,
//            textAlign = TextAlign.Center
//        )
//        Spacer(modifier = Modifier.height(60.dp))
//        Button(onClick = {/*TODO*/}) {
//            Text(stringResource(R.string.start_button_name))
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = {/*TODO*/}) {
//            Text(stringResource(R.string.saved_data_button_name))
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = {/*TODO*/}) {
//            Text(stringResource(R.string.settings_button_name))
//        }
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun MainActivityPreview(){
//    MenuButtons(modifier = Modifier
//        .fillMaxSize()
//        .wrapContentSize(Alignment.Center)
//    )
//}