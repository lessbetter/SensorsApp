package com.example.sensorsapp.ui.screens

import android.hardware.Sensor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.sensorsapp.ui.data.MeasurementViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.component.TextComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.RoundingMode
import kotlin.time.DurationUnit

@Composable
fun ResultScreen(
    modifier: Modifier,
    viewModel: MeasurementViewModel
){
    val modelProducer = remember{CartesianChartModelProducer()}
    val resultUiState by viewModel.resultUiState.collectAsState()

    val gravModel = viewModel.gravChartModel
    val gyroModel = viewModel.gyroChartProducer

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
        if(sensorsList.contains(Sensor.STRING_TYPE_GRAVITY)){
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Gravity"),
                    bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time"),
                    getXStep = { 0.5 }
                ),
                gravModel,
                scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0))

                )
        }
        if(sensorsList.contains(Sensor.STRING_TYPE_GYROSCOPE)){
            CartesianChartHost(
                rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Tilt"),
                    bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time"),
                    getXStep = { 0.5 }
                ),
                gyroModel,
                scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0))

                )
        }
        for(value in viewModel.collectedGravityData){
            Row (Modifier.fillMaxWidth()){
                Text(value.v0.toString())
                val rounded = value.timeMark!!.toDouble(DurationUnit.SECONDS).toBigDecimal().setScale(4,
                    RoundingMode.UP).toFloat()
                Text(rounded.toString())
            }
        }
    }

}