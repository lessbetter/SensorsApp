package com.example.sensorsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sensorsapp.ui.theme.SensorsAppTheme

class MainActivity : ComponentActivity() {
    //lateinit var sensorManager: SensorManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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