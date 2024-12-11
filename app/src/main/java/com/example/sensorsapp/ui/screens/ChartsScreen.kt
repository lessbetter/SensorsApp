package com.example.sensorsapp.ui.screens

import android.hardware.Sensor
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sensorsapp.ui.CreatingChartState
import com.example.sensorsapp.ui.data.MeasurementViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.component.TextComponent

@Composable
fun ChartsScreen(
    chartState: CreatingChartState,
    viewModel: MeasurementViewModel,
    modifier: Modifier
){
    when(chartState){
        is CreatingChartState.Success -> ResultScreen(modifier,viewModel)
        is CreatingChartState.Loading -> LoadingScreen(modifier.fillMaxSize())
        is CreatingChartState.Error -> Unit
    }
}

@Composable
fun ResultScreen(
    modifier: Modifier,
    viewModel: MeasurementViewModel
){
    Log.d("Loading: ","Enterred screen")
    val modelProducer = remember{CartesianChartModelProducer()}
    val resultUiState by viewModel.resultUiState.collectAsState()

    val gravModel = viewModel.gravChartModelProducer
    val gyroModel = viewModel.gyroChartModelProducer
    val magneModel = viewModel.magneChartModelProducer
    val acceModel = viewModel.acceChartModelProducer

    val sensorsList = viewModel.selectedSensors

//    LaunchedEffect(Unit){
//        withContext(Dispatchers.Default){
//            modelProducer.runTransaction {
//                lineSeries{ series(resultUiState.timeAxis,resultUiState.gravAxis) }
////                if(sensorsList.contains(Sensor.STRING_TYPE_GYROSCOPE)){
////                    lineSeries { series(resultUiState.gyroAxis,resultUiState.gravAxis) }
////                }
//            }
//        }
//
//    }
    Column (modifier = modifier.fillMaxSize()){
        if(sensorsList.contains(Sensor.TYPE_GRAVITY)){
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider =
                            LineCartesianLayer.LineProvider.series(
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Red))),
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Blue))),
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Green))),
                            )
                    ),
                    startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Gravity"),
                    bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time"),
                    getXStep = { 0.5 }
                ),
                gravModel,
                scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0)),
                modifier = Modifier.padding(top = 40.dp)

                )
        }
        if(sensorsList.contains(Sensor.TYPE_GYROSCOPE)){
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider =
                        LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Red))),
                            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Blue))),
                            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Green))),
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Tilt"),
                    bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time"),
                    getXStep = { 0.5 }
                ),
                gyroModel,
                scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0))

                )
        }
        if(sensorsList.contains(Sensor.TYPE_MAGNETIC_FIELD)){
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider =
                        LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Red))),
                            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Blue))),
                            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Green))),
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Geomagnetic field strength"),
                    bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time"),
                    getXStep = { 0.5 }
                ),
                gyroModel,
                scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0))

            )
        }
        if(sensorsList.contains(Sensor.TYPE_ACCELEROMETER)){
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider =
                        LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Red))),
                            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Blue))),
                            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Green))),
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Acceleration"),
                    bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time"),
                    getXStep = { 0.5 }
                ),
                gyroModel,
                scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0))

            )
        }
    }

}

@Composable
fun LoadingScreen(modifier: Modifier){
    Text("Loading")
}